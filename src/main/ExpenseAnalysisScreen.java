package main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;



import model.Expenses;
import dao.ExpenseDAO;


public class ExpenseAnalysisScreen extends Application {
    public static List<Stage> expenseAnalysisStage = new java.util.ArrayList<>();
    private List<Expenses> expenses = ExpenseDAO.getAllExpenses();

    private ScrollPane categoryScrollPane;
    private ScrollPane dayScrollPane;
    private BarChart<String, Number> categoryChart;
    private BarChart<String, Number> dayChart;
    private StackPane chartContainer;

    @Override
    public void start(Stage primaryStage) {
        // Main container with tab selection
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // 1. Analysis Type Selection
        ToggleGroup analysisType = new ToggleGroup();
        RadioButton categoryBtn = new RadioButton("Category-wise Analysis");
        RadioButton dayBtn = new RadioButton("Day/Month-wise Analysis");
        categoryBtn.setToggleGroup(analysisType);
        dayBtn.setToggleGroup(analysisType);
        categoryBtn.setSelected(true);

        HBox selectionBox = new HBox(20, categoryBtn, dayBtn);
        selectionBox.setPadding(new Insets(10));

        // 2. Chart container (will hold the active chart)
        chartContainer = new StackPane();
        chartContainer.setId("chartContainer");
        chartContainer.setPadding(new Insets(10));

        // 3. Initialize both charts with scroll panes
        categoryChart = createCategoryChart();
        dayChart = createDayChart();

        // Create scroll panes for each chart
        categoryScrollPane = createScrollPane(categoryChart);
        dayScrollPane = createScrollPane(dayChart);

        // 4. Date controls container
        VBox controlsContainer = new VBox(10);
        controlsContainer.setPadding(new Insets(10));

        // Category chart controls
        HBox dateRangeBox = new HBox(10);
        DatePicker startDatePicker = new DatePicker(LocalDate.now().withDayOfMonth(1));
        DatePicker endDatePicker = new DatePicker(LocalDate.now());
        Button updateCategoryBtn = new Button("Update");
        dateRangeBox.getChildren().addAll(
                new Label("From:"), startDatePicker,
                new Label("To:"), endDatePicker,
                updateCategoryBtn);

        // Day chart controls
        HBox monthSelectionBox = new HBox(10);
        ComboBox<String> monthCombo = new ComboBox<>(
                FXCollections.observableArrayList(
                        "January", "February", "March", "April", "May", "June",
                        "July", "August", "September", "October", "November", "December"));
        monthCombo.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
        Spinner<Integer> yearSpinner = new Spinner<>(2020, 2030, LocalDate.now().getYear());
        Button updateDayBtn = new Button("Update");
        monthSelectionBox.getChildren().addAll(
                new Label("Month:"), monthCombo,
                new Label("Year:"), yearSpinner,
                updateDayBtn);

        // Initially show category analysis
        showCategoryAnalysis();
        controlsContainer.getChildren().add(dateRangeBox);

        // Radio button actions
        categoryBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showCategoryAnalysis();
                controlsContainer.getChildren().clear();
                controlsContainer.getChildren().add(dateRangeBox);
            }
        });

        dayBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showDailyAnalysis();
                controlsContainer.getChildren().clear();
                controlsContainer.getChildren().add(monthSelectionBox);
            }
        });

        // Update button actions
        updateCategoryBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                updateCategoryChart(startDatePicker.getValue(), endDatePicker.getValue());
            }
        });

        updateDayBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                updateDayChart(monthCombo.getSelectionModel().getSelectedIndex() + 1, yearSpinner.getValue());
            }
        });

        // Add all components to root
        root.getChildren().addAll(selectionBox, controlsContainer, chartContainer);

        // Initial chart updates
        updateCategoryChart(startDatePicker.getValue(), endDatePicker.getValue());
        updateDayChart(monthCombo.getSelectionModel().getSelectedIndex() + 1,
                yearSpinner.getValue());

        // Show stage
        Scene scene = new Scene(root, 1000, 700);
        String cssPath = getClass().getResource("/css/expense_analysis_screen_style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        primaryStage.setTitle("Expense Analysis Charts");
        primaryStage.setHeight(650);
        primaryStage.setScene(scene);
        primaryStage.show();
        expenseAnalysisStage.add(primaryStage);
    }

    private ScrollPane createScrollPane(BarChart<String, Number> chart) {
        ScrollPane scrollPane = new ScrollPane(chart);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(false); // Allow horizontal scrolling
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    private BarChart<String, Number> createCategoryChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setId("categoryChart");        
        chart.setTitle("Expenses by Category");
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        // Set fixed bar width properties
        chart.setCategoryGap(20); // Space between categories
        chart.setBarGap(5); // Space between bars in same category

        // Make chart wider to ensure bars have fixed width
        chart.setMinWidth(1500);

        // Add value labels on top of bars
        chart.getData().addListener(new javafx.collections.ListChangeListener<XYChart.Series<String, Number>>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends XYChart.Series<String, Number>> c) {
                while (c.next()) {
                    for (XYChart.Series<String, Number> series : chart.getData()) {
                        for (XYChart.Data<String, Number> data : series.getData()) {
                            StackPane bar = (StackPane) data.getNode();
                            Label label = new Label(String.format("₹%.0f", data.getYValue()));
                            label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

                            bar.setOnMouseEntered(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent e) {
                                    if (!bar.getChildren().contains(label)) {
                                        bar.getChildren().add(label);
                                        StackPane.setAlignment(label, Pos.TOP_CENTER);
                                    }
                                }
                            });
                            bar.setOnMouseExited(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent e) {
                                    bar.getChildren().remove(label);
                                }
                            });
                        }
                    }
                }
            }
        });
                /* chart.getData().addListener(
                (javafx.collections.ListChangeListener.Change<? extends XYChart.Series<String, Number>> c) -> {
                    while (c.next()) {
                        for (XYChart.Series<String, Number> series : chart.getData()) {
                            for (XYChart.Data<String, Number> data : series.getData()) {
                                StackPane bar = (StackPane) data.getNode();
                                Label label = new Label(String.format("₹%.0f", data.getYValue()));
                                label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

                                bar.setOnMouseEntered(e -> {
                                    if (!bar.getChildren().contains(label)) {
                                        bar.getChildren().add(label);
                                        StackPane.setAlignment(label, Pos.TOP_CENTER);
                                    }
                                });
                                bar.setOnMouseExited(e -> bar.getChildren().remove(label));
                            }
                        }
                    }
                });
 */

        return chart;
    }

    private BarChart<String, Number> createDayChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setId("dayChart");
        chart.setTitle("Daily Expenses");
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        // Set fixed bar width properties
        chart.setCategoryGap(10); // Space between days
        chart.setBarGap(3); // Space between bars for same day

        // Make chart wider to ensure bars have fixed width
        chart.setMinWidth(2000);

        
        chart.getData().addListener(new javafx.collections.ListChangeListener<XYChart.Series<String, Number>>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends XYChart.Series<String, Number>> c) {
                while (c.next()) {
                    for (XYChart.Series<String, Number> series : chart.getData()) {
                        for (XYChart.Data<String, Number> data : series.getData()) {
                            StackPane bar = (StackPane) data.getNode();
                            Label label = new Label(String.format("₹%.0f", data.getYValue()));
                            label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                            bar.setOnMouseEntered(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent e) {
                                    if (!bar.getChildren().contains(label)) {
                                        bar.getChildren().add(label);
                                        StackPane.setAlignment(label, Pos.TOP_CENTER);
                                    }
                                }
                            });
                            bar.setOnMouseExited(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent e) {
                                    bar.getChildren().remove(label);
                                }
                            });
                        }
                    }
                }
            }
        });
        /* chart.getData().addListener(
                (javafx.collections.ListChangeListener.Change<? extends XYChart.Series<String, Number>> c) -> {
                    while (c.next()) {
                        for (XYChart.Series<String, Number> series : chart.getData()) {
                            for (XYChart.Data<String, Number> data : series.getData()) {
                                StackPane bar = (StackPane) data.getNode();
                                Label label = new Label(String.format("₹%.0f", data.getYValue()));
                                label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

                                bar.setOnMouseEntered(e -> {
                                    if (!bar.getChildren().contains(label)) {
                                        bar.getChildren().add(label);
                                        StackPane.setAlignment(label, Pos.TOP_CENTER);
                                    }
                                });
                                bar.setOnMouseExited(e -> bar.getChildren().remove(label));
                            }
                        }
                    }
                });
        */

        return chart;
    }

    private void showCategoryAnalysis() {
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(categoryScrollPane);
    }

    private void showDailyAnalysis() {
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(dayScrollPane);
    }

    private void updateCategoryChart(LocalDate start, LocalDate end) {
        Map<String, Double> categoryTotals = expenses.stream()
        .filter(new Predicate<Expenses>() {
            @Override
            public boolean test(Expenses e) {
                return !e.getE_date().toLocalDateTime().toLocalDate().isBefore(start) && !e.getE_date().toLocalDateTime().toLocalDate().isAfter(end);
            }
        })
        .collect(Collectors.groupingBy(new Function<Expenses, String>() {
            @Override
            public String apply(Expenses e) {
                return e.getCategory();
            }
        }, Collectors.summingDouble(new ToDoubleFunction<Expenses>() {
            @Override
            public double applyAsDouble(Expenses e) {
                return e.getAmount();
            }
        })));
                /* .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
                .collect(Collectors.groupingBy(
                        Expenses::getCategory,
                        Collectors.summingDouble(Expenses::getAmount)));*/


      
        XYChart.Series<String, Number> series = new XYChart.Series<>();
       //categoryTotals.forEach((category, total) -> series.getData().add(new XYChart.Data<>(category, total)));
        categoryTotals.forEach(new BiConsumer<String, Double>() {
            @Override
            public void accept(String category, Double total) {
                series.getData().add(new XYChart.Data<>(category, total));
            }
        });

        categoryChart.getData().clear();
        categoryChart.getData().add(series);
    }

    private void updateDayChart(int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        Map<Integer, Double> dailyTotals = expenses.stream()
        .filter(new Predicate<Expenses>() {
            @Override
            public boolean test(Expenses e) {
                return !e.getE_date().toLocalDateTime().toLocalDate().isBefore(start) && !e.getE_date().toLocalDateTime().toLocalDate().isAfter(end);
            }
        })
        .collect(Collectors.groupingBy(
                new Function<Expenses, Integer>() {
                    @Override
                    public Integer apply(Expenses e) {
                        return e.getE_date().toLocalDateTime().toLocalDate().getDayOfMonth();
                    }
                },
                Collectors.summingDouble(new ToDoubleFunction<Expenses>() {
                    @Override
                    public double applyAsDouble(Expenses e) {
                        return e.getAmount();
                    }
                })));
              /*  .filter(e -> !e.getE_date().isBefore(start) && !e.getE_date().isAfter(end))
                .collect(Collectors.groupingBy(
                        e -> e.getE_date().getDayOfMonth(),
                        Collectors.summingDouble(Expenses::getAmount)));*/

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            double amount = dailyTotals.getOrDefault(day, 0.0);
            series.getData().add(new XYChart.Data<>(String.valueOf(day), amount));
        }

        dayChart.getData().clear();
        dayChart.getData().add(series);
    }

    public static void main(String[] args) {
        launch(args);
    }
}