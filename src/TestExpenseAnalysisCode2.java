import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class TestExpenseAnalysisCode2 extends Application {

    public static class Expense {
        private String category;
        private double amount;
        private LocalDate date;

        public Expense(String category, double amount, LocalDate date) {
            this.category = category;
            this.amount = amount;
            this.date = date;
        }

        public String getCategory() { return category; }
        public double getAmount() { return amount; }
        public LocalDate getDate() { return date; }
    }

    private List<Expense> expenses = Arrays.asList(
        new Expense("Food", 500.0, LocalDate.of(2025, 7, 1)),
        new Expense("Transport", 300.0, LocalDate.of(2025, 7, 1)),
        new Expense("Food", 200.0, LocalDate.of(2025, 7, 2)),
        new Expense("Shopping", 1000.0, LocalDate.of(2025, 7, 3)),
        new Expense("Transport", 150.0, LocalDate.of(2025, 7, 3)),
        new Expense("Food", 350.0, LocalDate.of(2025, 7, 15)),
        new Expense("Entertainment", 800.0, LocalDate.of(2025, 7, 20))
    );

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
        RadioButton dayBtn = new RadioButton("Daily Analysis");
        categoryBtn.setToggleGroup(analysisType);
        dayBtn.setToggleGroup(analysisType);
        categoryBtn.setSelected(true);

        HBox selectionBox = new HBox(20, categoryBtn, dayBtn);
        selectionBox.setPadding(new Insets(10));

        // 2. Chart container (will hold the active chart)
        chartContainer = new StackPane();
        chartContainer.setPadding(new Insets(10));

        // 3. Initialize both charts
        categoryChart = createCategoryChart();
        dayChart = createDayChart();
       
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
            updateCategoryBtn
        );

        // Day chart controls
        HBox monthSelectionBox = new HBox(10);
        ComboBox<String> monthCombo = new ComboBox<>(
            FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )
        );
        monthCombo.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
        Spinner<Integer> yearSpinner = new Spinner<>(2020, 2030, LocalDate.now().getYear());
        Button updateDayBtn = new Button("Update");
        monthSelectionBox.getChildren().addAll(
            new Label("Month:"), monthCombo,
            new Label("Year:"), yearSpinner,
            updateDayBtn
        );

        // Initially show category analysis
        showCategoryAnalysis();
        controlsContainer.getChildren().add(dateRangeBox);

        // Radio button actions
        categoryBtn.setOnAction(e -> {
            showCategoryAnalysis();
            controlsContainer.getChildren().clear();
            controlsContainer.getChildren().add(dateRangeBox);
        });

        dayBtn.setOnAction(e -> {
            showDailyAnalysis();
            controlsContainer.getChildren().clear();
            controlsContainer.getChildren().add(monthSelectionBox);
        });

        // Update button actions
        updateCategoryBtn.setOnAction(e -> updateCategoryChart(
            startDatePicker.getValue(), 
            endDatePicker.getValue()
        ));

        updateDayBtn.setOnAction(e -> updateDayChart(
            monthCombo.getSelectionModel().getSelectedIndex() + 1,
            yearSpinner.getValue()
        ));

        // Add all components to root
        root.getChildren().addAll(selectionBox, controlsContainer, chartContainer);

        // Initial chart updates
        updateCategoryChart(startDatePicker.getValue(), endDatePicker.getValue());
        updateDayChart(monthCombo.getSelectionModel().getSelectedIndex() + 1, 
                     yearSpinner.getValue());

        // Show stage
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Expense Analysis Charts");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private BarChart<String, Number> createCategoryChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Expenses by Category");
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        
        // Add value labels on top of bars
        chart.getData().addListener((javafx.collections.ListChangeListener.Change<? extends XYChart.Series<String, Number>> c) -> {
            while (c.next()) {
                for (XYChart.Series<String, Number> series : chart.getData()) {
                    for (XYChart.Data<String, Number> data : series.getData()) {
                        Label label = new Label(String.format("₹%.0f", data.getYValue()));
                        StackPane.setAlignment(label, Pos.TOP_CENTER);
                        data.getNode().setOnMouseEntered(e -> {
                            StackPane bar = (StackPane) data.getNode();
                            if (!bar.getChildren().contains(label)) {
                                bar.getChildren().add(label);
                            }
                        });
                        data.getNode().setOnMouseExited(e -> {
                            StackPane bar = (StackPane) data.getNode();
                            bar.getChildren().remove(label);
                        });
                    }
                }
            }
        });
        
        return chart;
    }

    private BarChart<String, Number> createDayChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Daily Expenses");
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        
        // Add value labels on top of bars
        chart.getData().addListener((javafx.collections.ListChangeListener.Change<? extends XYChart.Series<String, Number>> c) -> {
            while (c.next()) {
                for (XYChart.Series<String, Number> series : chart.getData()) {
                    for (XYChart.Data<String, Number> data : series.getData()) {
                        Label label = new Label(String.format("₹%.0f", data.getYValue()));
                        StackPane.setAlignment(label, Pos.TOP_CENTER);
                        data.getNode().setOnMouseEntered(e -> {
                            StackPane bar = (StackPane) data.getNode();
                            if (!bar.getChildren().contains(label)) {
                                bar.getChildren().add(label);
                            }
                        });
                        data.getNode().setOnMouseExited(e -> {
                            StackPane bar = (StackPane) data.getNode();
                            bar.getChildren().remove(label);
                        });
                    }
                }
            }
        });
        
        return chart;
    }

    private void showCategoryAnalysis() {
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(categoryChart);
    }

    private void showDailyAnalysis() {
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(dayChart);
    }

    private void updateCategoryChart(LocalDate start, LocalDate end) {
        Map<String, Double> categoryTotals = expenses.stream()
            .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
            .collect(Collectors.groupingBy(
                Expense::getCategory,
                Collectors.summingDouble(Expense::getAmount)
            ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        categoryTotals.forEach((category, total) -> 
            series.getData().add(new XYChart.Data<>(category, total))
        );

        categoryChart.getData().clear();
        categoryChart.getData().add(series);
    }

    private void updateDayChart(int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        Map<Integer, Double> dailyTotals = expenses.stream()
            .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
            .collect(Collectors.groupingBy(
                e -> e.getDate().getDayOfMonth(),
                Collectors.summingDouble(Expense::getAmount)
            ));

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