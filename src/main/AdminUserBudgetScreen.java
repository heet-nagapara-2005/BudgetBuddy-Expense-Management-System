
package main;

import model.Budgets;

import model.User;

import dao.ManageBudgetDAO;
import dao.UserDAO;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class AdminUserBudgetScreen extends Application {
    public static List<Stage> adminUserBudgetStage = new java.util.ArrayList<>();
    private TableView<User> userTable = new TableView<>();
    private ObservableList<User> userData;
    private ObservableList<Budgets> budgets = FXCollections.observableArrayList();
    private TableView<Budgets> budgetTable = new TableView<>();


    private DatePicker startDatePicker = new DatePicker();
    private DatePicker endDatePicker = new DatePicker();
    private int currentUser;

    private ComboBox<String> categoryField = new ComboBox<>();
    private ProgressBar budgetProgressBar = new ProgressBar();
    private Label remainingLabel = new Label();
    private Label usedLabel = new Label();
    private PieChart budgetChart = new PieChart();
    private ObservableList<Budgets> budgetList;

    boolean flag = false;

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
        stage.setTitle("User Budgets");

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
        if(!flag){
            userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            flag = true;
        }

        userTable.setItems(filteredData);

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
                getClass().getResource("/css/admin_user_budget_screen_style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        adminUserBudgetStage.add(stage);
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
                    private final Button viewBtn = new Button("View Budgets");

                    {
                        viewBtn.getStyleClass().addAll("button", "view-button");

                        viewBtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                User selectedUser = getTableView().getItems().get(getIndex());
                                currentUser = selectedUser.getId();
                                openBudgetScreen(selectedUser);
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

    private void openBudgetScreen(User user) {
        Stage budgetStage = new Stage();
        budgetStage.setTitle("Budgets for " + user.getName());

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        // Add sample data
        initializeSampleData();

        HBox bottomPanel = createBottomPanel();
        mainLayout.setBottom(bottomPanel);
        budgetProgressBar.setProgress(0);
        // Create and configure top panel (form)

        HBox temp = new HBox();
        temp.getChildren().addAll(bottomPanel);
        mainLayout.setTop(temp);

        // Create and configure center panel (table)
        VBox tablePanel = createTablePanel();
        mainLayout.setCenter(tablePanel);
        mainLayout.getStyleClass().add("root");

        // Create and configure bottom panel (stats and chart)
        // HBox bottomPanel = createBottomPanel();
        // mainLayout.setBottom(bottomPanel);

        // Set up event handlers
        setupEventHandlers();

        // Show the stage
        Scene scene = new Scene(mainLayout, 900, 600);
        scene.getStylesheets().add(
                getClass().getResource("/css/admin_user_budget_screen_style.css").toExternalForm());
        Stage primaryStage = new Stage();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Budgets of" + user.getName());
        primaryStage.show();
    }

    private VBox createTablePanel() {
        VBox tablePanel = new VBox(10);
        tablePanel.setId("table-panel");

        tablePanel.setPadding(new Insets(10));

        // Table setup
        TableColumn<Budgets, String> budgetName = new TableColumn<>("Budget Name");
        budgetName.setCellValueFactory(new PropertyValueFactory<>("budgetName"));

        TableColumn<Budgets, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Budgets, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Budgets, LocalDate> startDateCol = new TableColumn<>("Start Date");
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<Budgets, LocalDate> endDateCol = new TableColumn<>("End Date");
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<Budgets, Double> currentSpent = new TableColumn<>("Current Spent");
        currentSpent.setCellValueFactory(new PropertyValueFactory<>("currentSpent"));

        TableColumn<Budgets, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Budgets, Timestamp> CreatedAt = new TableColumn<>("Created At");
        CreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        /*--------------------------------------------------------------------------------------- */
        amountCol.setCellFactory(new Callback<TableColumn<Budgets, Double>, TableCell<Budgets, Double>>() {
            @Override
            public TableCell<Budgets, Double> call(TableColumn<Budgets, Double> param) {
                return new TableCell<Budgets, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.toString());
                            setAlignment(Pos.CENTER_RIGHT); // <<< Right Align
                            setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-align: right;"); // <<< Extra CSS for Right
                                                                                             // Align

                        }
                    }
                };
            }
        });
        currentSpent.setCellFactory(new Callback<TableColumn<Budgets, Double>, TableCell<Budgets, Double>>() {
            @Override
            public TableCell<Budgets, Double> call(TableColumn<Budgets, Double> param) {
                return new TableCell<Budgets, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.toString());
                            setAlignment(Pos.CENTER_RIGHT); // <<< Right Align
                            setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-align: right;");
                        }
                    }
                };
            }
        });
        // Center Alignment for Start Date Column
        // Center Alignment for Start Date Column (LocalDate type)
        startDateCol.setCellFactory(new Callback<TableColumn<Budgets, LocalDate>, TableCell<Budgets, LocalDate>>() {
            // Formatter ko yahan define karein taaki har baar naya object na bane
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public TableCell<Budgets, LocalDate> call(TableColumn<Budgets, LocalDate> param) {
                return new TableCell<Budgets, LocalDate>() {
                    @Override
                    protected void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            // LocalDate ko format karke String mein set kiya
                            setText(formatter.format(item));
                            setAlignment(Pos.CENTER); // Center Align
                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                };
            }
        });

        // Center Alignment for End Date Column
        // Center Alignment for End Date Column (LocalDate type)
        endDateCol.setCellFactory(new Callback<TableColumn<Budgets, LocalDate>, TableCell<Budgets, LocalDate>>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public TableCell<Budgets, LocalDate> call(TableColumn<Budgets, LocalDate> param) {
                return new TableCell<Budgets, LocalDate>() {
                    @Override
                    protected void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            // LocalDate ko format karke String mein set kiya
                            setText(formatter.format(item));
                            setAlignment(Pos.CENTER); // Center Align
                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                };
            }
        });

        // Center Alignment for Created At Column
        // Center Alignment for Created At Column (LocalDate type)
        CreatedAt.setCellFactory(new Callback<TableColumn<Budgets, Timestamp>, TableCell<Budgets, Timestamp>>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public TableCell<Budgets, Timestamp> call(TableColumn<Budgets, Timestamp> param) {
                return new TableCell<Budgets, Timestamp>() {
                    @Override
                    protected void updateItem(Timestamp item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            // Timestamp ko LocalDateTime mein convert karke format kiya
                            setText(formatter.format(item.toLocalDateTime().toLocalDate()));
                            setAlignment(Pos.CENTER); // Center Align
                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                };
            }
        });

        statusCol.setCellFactory(new Callback<TableColumn<Budgets, String>, TableCell<Budgets, String>>() {
            @Override
            public TableCell<Budgets, String> call(TableColumn<Budgets, String> column) {
                return new TableCell<Budgets, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle(null); // Clear style when empty
                        } else {
                            setText(item);

                            // Text aur Cell content ko Center Align karein
                            setAlignment(Pos.CENTER);
                            setStyle("-fx-alignment: CENTER;"); // CSS fallback/reinforcement
                        }
                    }
                };
            }
        });
        /*----------------------------------------------------------------------------------------- */

        // Add delete button to each row
        if (budgetTable.getColumns().isEmpty()) {
            budgetTable.getColumns().add(budgetName);
            budgetTable.getColumns().add(categoryCol);
            budgetTable.getColumns().add(amountCol);
            budgetTable.getColumns().add(startDateCol);
            budgetTable.getColumns().add(endDateCol);
            budgetTable.getColumns().add(currentSpent);
            budgetTable.getColumns().add(statusCol);
            budgetTable.getColumns().add(CreatedAt);
        
            budgetTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        }
        
        /*
        budgetTable.getColumns().clear();
        budgetTable.getColumns().add(budgetName);
        budgetTable.getColumns().add(categoryCol);
        budgetTable.getColumns().add(amountCol);
        budgetTable.getColumns().add(startDateCol);
        budgetTable.getColumns().add(endDateCol);
        budgetTable.getColumns().add(currentSpent);
        budgetTable.getColumns().add(statusCol);
        budgetTable.getColumns().add(CreatedAt);
        budgetTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        */
        



        budgetTable.setItems(budgets);

        budgetTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Budgets>() {
            @Override
            public void changed(ObservableValue<? extends Budgets> obs, Budgets oldSelection, Budgets newSelection) {
                if (newSelection != null) {
                    // Jab record select hoga to disable
                    categoryField.setDisable(true);
                } else {
                    // Jab selection hata dega to enable
                    categoryField.setDisable(false);
                }
            }
        });

        // Add table to panel
        tablePanel.getChildren().add(budgetTable);

        return tablePanel;
    }

    private HBox createBottomPanel() {
        HBox bottomPanel = new HBox(20);
        bottomPanel.setPadding(new Insets(20));
        bottomPanel.setAlignment(Pos.CENTER);

        // Progress section
        VBox progressBox = new VBox(10);
        progressBox.setId("progress-box");

        progressBox.setAlignment(Pos.CENTER_LEFT);
        progressBox.setPadding(new Insets(10));
        progressBox.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #c8e6c9; -fx-border-radius: 5;");

        Label progressTitle = new Label("Budget Progress");
        progressTitle.setStyle("-fx-font-weight: bold;");

        budgetProgressBar.setPrefWidth(300);
        budgetProgressBar.setPrefHeight(20);

        HBox usedBox = new HBox(5);
        usedBox.getChildren().addAll(new Label("Used:"), usedLabel);

        HBox remainingBox = new HBox(5);
        remainingBox.getChildren().addAll(new Label("Remaining:"), remainingLabel);

        progressBox.getChildren().addAll(progressTitle, budgetProgressBar, usedBox, remainingBox);

        // Chart section
        VBox chartBox = new VBox(10);
        chartBox.setId("chart-box");

        chartBox.setAlignment(Pos.CENTER);
        chartBox.setPadding(new Insets(10));
        chartBox.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #bbdefb; -fx-border-radius: 5;");

        Label chartTitle = new Label("Budget Distribution");
        chartTitle.setStyle("-fx-font-weight: bold;");

        budgetChart.setPrefSize(300, 200);
        budgetChart.setLegendVisible(true);

        chartBox.getChildren().addAll(chartTitle, budgetChart);

        bottomPanel.getChildren().addAll(progressBox, chartBox);

        return bottomPanel;
    }

    private void setupEventHandlers() {
        // Update progress and chart when table selection changes
        budgetTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Budgets>() {
            @Override
            public void changed(ObservableValue<? extends Budgets> observable, Budgets oldValue, Budgets newValue) {
                if (newValue != null) {
                    updateProgressAndChart(newValue);
                }
            }
        });

        // Initialize date pickers with reasonable defaults
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusMonths(1));
    }

    private void updateProgressAndChart(Budgets budget) {
        // In a real application, you would calculate actual spending vs budget
        double usedAmount = budget.getCurrentSpent(); // Simulate 65% used
        double remaining = budget.getAmount() - usedAmount;
        double progress = usedAmount / budget.getAmount();

        budgetProgressBar.setProgress(progress);

        // Change color based on usage
        if (progress > 0.9) {
            budgetProgressBar.setStyle("-fx-accent: #ff4444;"); // Red
        } else if (progress > 0.7) {
            budgetProgressBar.setStyle("-fx-accent: #ffbb33;"); // Yellow
        } else {
            budgetProgressBar.setStyle("-fx-accent: #00C851;"); // Green
        }

        usedLabel.setText(String.format("₹%.2f", usedAmount));
        remainingLabel.setText(String.format("₹%.2f", remaining));
    }

    private void updateChartData() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Budgets budget : budgets) {
            pieChartData.add(new PieChart.Data(
                    budget.getCategory() + " (₹" + budget.getAmount() + ")",
                    budget.getAmount()));
        }

        budgetChart.setData(pieChartData);

        // Set colors for pie chart slices
        int i = 0;
        Color[] colors = { Color.web("#4285F4"), Color.web("#EA4335"), Color.web("#FBBC05"),
                Color.web("#34A853"), Color.web("#673AB7"), Color.web("#FF9800") };

        for (PieChart.Data data : budgetChart.getData()) {
            data.getNode().setStyle("-fx-pie-color: " + colors[i % colors.length].toString().replace("0x", "#") + ";");
            i++;
        }
    }

    private void initializeSampleData() {
        List<Budgets> data = ManageBudgetDAO.getAllBudgetsForUser(currentUser);
        budgetList = FXCollections.observableArrayList(data);
        budgets.setAll(budgetList);

        updateChartData();
    }

}
