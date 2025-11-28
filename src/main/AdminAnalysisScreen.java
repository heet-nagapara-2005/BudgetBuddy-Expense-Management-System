
package main;

import model.Expenses;
import model.User;
import dao.ExpenseDAO;

import dao.UserDAO;
import javafx.application.Application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class AdminAnalysisScreen extends Application {
    public static List<Stage> adminAnalysisStage = new java.util.ArrayList<>();
    private List<Expenses> expenses;

    private ScrollPane categoryScrollPane;
    private ScrollPane dayScrollPane;
    private BarChart<String, Number> categoryChart;
    private BarChart<String, Number> dayChart;
    private StackPane chartContainer;

    private TableView<User> userTable = new TableView<>();
    private ObservableList<User> userData;
    private int currentUser;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initializeData();
        setupUI(primaryStage);
    }

    private void initializeData() {
        userData = FXCollections.observableArrayList(UserDAO.getAllUsers());
        userTable.setItems(userData);
    }

    private void setupUI(Stage stage) {
        stage.setTitle("User Expenses Analysis");

        // Columns
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Actions column (View Expenses)
        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(createActionCellFactory());

        /*--------------------------------------------------------------------------------------------- */
        nameCol.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            public TableCell<User, String> call(TableColumn<User, String> param) {
                return new TableCell<User, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item);
                            // Center Left Alignment
                            // setAlignment(Pos.CENTER_LEFT);
                            setStyle("-fx-alignment: center-left ;");

                        }
                    }
                };
            }
        });

        emailCol.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            public TableCell<User, String> call(TableColumn<User, String> param) {
                return new TableCell<User, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item);
                            // Center Left Alignment
                            // setAlignment(Pos.CENTER_LEFT);
                            setStyle("-fx-alignment: center-left ;");

                        }
                    }
                };
            }
        });
        /*--------------------------------------------------------------------------------------------- */

        // Filtered list for searching
        FilteredList<User> filteredData = new FilteredList<>(userData, new Predicate<User>() {
            @Override
            public boolean test(User u) {
                return true;
            }
        });

        userTable.getColumns().add(nameCol);
        userTable.getColumns().add(emailCol);
        userTable.getColumns().add(actionsCol);
        userTable.setItems(filteredData);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Search Bar
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search across all columns...");
        searchBar.setPrefWidth(300);

        searchBar.textProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String oldValue,
                    String newValue) {
                filteredData.setPredicate(new Predicate<User>() {
                    @Override
                    public boolean test(User user) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true; // show all
                        }
                        String lowerCaseFilter = newValue.toLowerCase();
                        return user.getName().toLowerCase().contains(lowerCaseFilter)
                                || user.getEmail().toLowerCase().contains(lowerCaseFilter);
                    }
                });
            }
        });

        // Add column-specific filter context menus
        addColumnFilterContextMenu(nameCol, new Function<User, String>() {
            @Override
            public String apply(User user) {
                return user.getName();
            }
        });

        addColumnFilterContextMenu(emailCol, new Function<User, String>() {
            @Override
            public String apply(User user) {
                return user.getEmail();
            }
        });

        VBox layout = new VBox(10, searchBar, userTable);
        layout.setPadding(new Insets(10));
        layout.getStyleClass().add("root");

        Scene scene = new Scene(layout, 500, 400);
        scene.getStylesheets().add(
                getClass().getResource("/css/admin_user_anlysis_screen_style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
        adminAnalysisStage.add(stage);
    }

    private void addColumnFilterContextMenu(TableColumn<User, ?> column,
            final Function<User, String> extractor) {

        ContextMenu contextMenu = new ContextMenu();

        MenuItem filterItem = new MenuItem("Filter this column...");
        filterItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Filter " + column.getText());
                dialog.setHeaderText("Enter text to filter the " + column.getText() + " column:");
                dialog.showAndWait().ifPresent(filterText -> {
                    FilteredList<User> filteredList = (FilteredList<User>) column.getTableView().getItems();
                    filteredList.setPredicate(new Predicate<User>() {
                        @Override
                        public boolean test(User user) {
                            if (filterText.isEmpty())
                                return true;
                            return extractor.apply(user).toLowerCase().contains(filterText.toLowerCase());
                        }
                    });
                });
            }
        });

        MenuItem clearFilterItem = new MenuItem("Clear column filter");
        clearFilterItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FilteredList<User> filteredList = (FilteredList<User>) column.getTableView().getItems();
                filteredList.setPredicate(null);
            }
        });

        contextMenu.getItems().addAll(filterItem, clearFilterItem);
        column.setContextMenu(contextMenu);
    }

    private Callback<TableColumn<User, Void>, TableCell<User, Void>> createActionCellFactory() {
        return new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<User, Void>() {
                    private final Button viewBtn = new Button("View Charts");

                    {
                        viewBtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                User selectedUser = getTableView().getItems().get(getIndex());
                                currentUser = selectedUser.getId();
                                expenses = ExpenseDAO.getAllExpensesById(currentUser);
                                openAnalysisScreen(selectedUser);
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(5, viewBtn);
                            buttons.setAlignment(Pos.CENTER);

                            setGraphic(buttons);
                            setAlignment(Pos.CENTER);

                        }
                    }
                };
            }
        };
    }

    private void openAnalysisScreen(User user) {

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
        scene.getStylesheets().add(
            getClass().getResource("/css/admin_user_anlysis_screen_style.css").toExternalForm());

  
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Expense Analysis Charts" + " - User: " + user.getName());
        primaryStage.setHeight(650);
        primaryStage.setScene(scene);
        primaryStage.show();
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
            public void onChanged(
                    javafx.collections.ListChangeListener.Change<? extends XYChart.Series<String, Number>> c) {
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

        chart.setCategoryGap(10);
        chart.setBarGap(3);

        chart.setMinWidth(2000);

        chart.getData().addListener(new javafx.collections.ListChangeListener<XYChart.Series<String, Number>>() {
            @Override
            public void onChanged(
                    javafx.collections.ListChangeListener.Change<? extends XYChart.Series<String, Number>> c) {
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
                        return !e.getE_date().toLocalDateTime().toLocalDate().isBefore(start)
                                && !e.getE_date().toLocalDateTime().toLocalDate().isAfter(end);
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

        XYChart.Series<String, Number> series = new XYChart.Series<>();
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
                        return !e.getE_date().toLocalDateTime().toLocalDate().isBefore(start)
                                && !e.getE_date().toLocalDateTime().toLocalDate().isAfter(end);
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

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            double amount = dailyTotals.getOrDefault(day, 0.0);
            series.getData().add(new XYChart.Data<>(String.valueOf(day), amount));
        }

        dayChart.getData().clear();
        dayChart.getData().add(series);
    }

}
