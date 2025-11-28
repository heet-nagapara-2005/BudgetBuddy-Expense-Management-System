package main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
//import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import database.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

//import java.time.format.DateTimeFormatter;
//import java.util.Optional;
//import java.util.Locale.Category;
import dao.CategoryDAO;
import dao.ExpenseDAO;
import dao.ManageBudgetDAO;

//import jakarta.mail.Session;
import utils.Session;
import model.Budgets;
import model.Category;

public class ManageBudgetScreen extends Application {
    public static List<Stage> budgetStage = new java.util.ArrayList<>();
    private ObservableList<Budgets> budgets = FXCollections.observableArrayList();
    private TableView<Budgets> budgetTable = new TableView<>();
    private DatePicker startDatePicker = new DatePicker();
    private DatePicker endDatePicker = new DatePicker();
    private TextField amountField = new TextField();
    // private TextField categoryField = new TextField();
    private ComboBox<String> categoryField = new ComboBox<>();
    private TextField budgetNameField = new TextField();
    private CheckBox activeCheckBox = new CheckBox("Active");
    private ProgressBar budgetProgressBar = new ProgressBar();
    private Label remainingLabel = new Label();
    private Label usedLabel = new Label();
    private PieChart budgetChart = new PieChart();
    private ObservableList<Budgets> budgetList;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Budget Management");

        // Create main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        // Add sample data
        initializeSampleData();

        HBox bottomPanel = createBottomPanel();
        mainLayout.setBottom(bottomPanel);
        // Create and configure top panel (form)
        GridPane formPanel = createFormPanel();
        HBox temp = new HBox();
        temp.getChildren().addAll(formPanel, bottomPanel);
        mainLayout.setTop(temp);

        // Create and configure center panel (table)
        VBox tablePanel = createTablePanel();
        mainLayout.setCenter(tablePanel);

        // Create and configure bottom panel (stats and chart)
        // HBox bottomPanel = createBottomPanel();
        // mainLayout.setBottom(bottomPanel);

        // Set up event handlers
        setupEventHandlers();

        // Show the stage
        Scene scene = new Scene(mainLayout, 1100, 620);
        String cssPath = getClass().getResource("/css/manage_budget_screen_style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        primaryStage.setScene(scene);
        primaryStage.show();
        budgetStage.add(primaryStage);
    }

    private GridPane createFormPanel() {
        GridPane formPanel = new GridPane();
        formPanel.setId("form-panel");

        formPanel.setAlignment(Pos.CENTER);
        formPanel.setHgap(10);
        formPanel.setVgap(10);
        formPanel.setPadding(new Insets(10));
        formPanel.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        // Form elements
        Label titleLabel = new Label("Add/Edit Budget");
        // titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.getStyleClass().add("label-title");
        formPanel.add(titleLabel, 0, 0, 2, 1);

        formPanel.add(new Label("Budget Name:"), 0, 1);
        formPanel.add(budgetNameField, 1, 1);

        formPanel.add(new Label("Category:"), 0, 2);
        // formPanel.add(categoryField, 1, 2);
        formPanel.add(categoryField, 1, 2);
        categoryField.setPromptText("Select Category");
        setCategory(); // Load categories into the ComboBox

        formPanel.add(new Label("Amount:"), 0, 3);
        formPanel.add(amountField, 1, 3);

        formPanel.add(new Label("Start Date:"), 0, 4);
        formPanel.add(startDatePicker, 1, 4);

        formPanel.add(new Label("End Date:"), 0, 5);
        formPanel.add(endDatePicker, 1, 5);

        formPanel.add(activeCheckBox, 1, 6);
        activeCheckBox.setSelected(true);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button clearButton = new Button("Clear");
        addButton.setId("add-button"); // Uses #add-button (Primary Blue)
        updateButton.getStyleClass().add("button-secondary"); // Uses .button-secondary (Gray)
        clearButton.getStyleClass().add("button-secondary"); // Uses .button-secondary (Gray)

        buttonBox.getChildren().addAll(addButton, updateButton, clearButton);
        formPanel.add(buttonBox, 1, 7);

        // Button actions
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addBudget();
            }
        });
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateBudget();
            }
        });
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearForm();
            }
        });

        return formPanel;
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

        /*
         * // old working code
         * TableColumn<Budgets, String> startDateCol = new TableColumn<>("Start Date");
         * startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
         * 
         * TableColumn<Budgets, String> endDateCol = new TableColumn<>("End Date");
         * endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
         */
        TableColumn<Budgets, LocalDate> startDateCol = new TableColumn<>("Start Date");
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<Budgets, LocalDate> endDateCol = new TableColumn<>("End Date");
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<Budgets, Double> currentSpent = new TableColumn<>("Current Spent");
        currentSpent.setCellValueFactory(new PropertyValueFactory<>("currentSpent"));

        TableColumn<Budgets, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        /*
         * // old working code
         * TableColumn<Budgets, String> CreatedAt = new TableColumn<>("Created At");
         * CreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
         */
        TableColumn<Budgets, Timestamp> CreatedAt = new TableColumn<>("Created At");
        CreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        TableColumn<Budgets, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setSortable(false);

        /*----------------------------------------------------------------------------------------- */
        /*----------------------------------------------------------------------------------------- */
        /*----------------------------------------------------------------------------------------- */
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
        /*----------------------------------------------------------------------------------------- */
        /*----------------------------------------------------------------------------------------- */
        // Add delete button to each row
        Callback<TableColumn<Budgets, Void>, TableCell<Budgets, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Budgets, Void> call(final TableColumn<Budgets, Void> param) {
                return new TableCell<>() {
                    private final Button deleteBtn = new Button("Delete");
                    // private final Button historyBtn = new Button("History");

                    {
                        deleteBtn.setId("delete-button-row");
                        // deleteBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
                        deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                Budgets budget = getTableView().getItems().get(getIndex());
                                deleteBudget(budget);
                                initializeSampleData();
                                updateChartData();

                            }
                        });

                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(5, deleteBtn);
                            buttons.setAlignment(Pos.CENTER);
                            setGraphic(buttons);
                            setAlignment(Pos.CENTER);
                            setStyle("-fx-alignment: CENTER;");

                        }
                    }
                };
            }
        };
        actionsCol.setCellFactory(cellFactory);

        // budgetTable.getColumns().addAll(categoryCol, amountCol, startDateCol,
        // endDateCol, statusCol, actionsCol);
        budgetTable.getColumns().add(budgetName);
        budgetTable.getColumns().add(categoryCol);
        budgetTable.getColumns().add(amountCol);
        budgetTable.getColumns().add(startDateCol);
        budgetTable.getColumns().add(endDateCol);
        budgetTable.getColumns().add(currentSpent);
        budgetTable.getColumns().add(statusCol);
        budgetTable.getColumns().add(CreatedAt);
        budgetTable.getColumns().add(actionsCol);
        budgetTable.setItems(budgets);
        budgetTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

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
        // progressBox.setStyle("-fx-background-color: #e8f5e9; -fx-border-color:
        // #c8e6c9; -fx-border-radius: 5;");

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
        // chartBox.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #bbdefb;
        // -fx-border-radius: 5;");

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

    private void addBudget() {
        try {
            String query = "SELECT c_id FROM category WHERE c_name = ? AND u_id = ?";
            Connection connection = DBConnection.getDBConnection();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, categoryField.getValue());
            pstmt.setInt(2, Session.getCurrentUser());
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                showAlert(AlertType.ERROR, "Error", "Category not found");
                return;
            }
            int categoryId = rs.getInt("c_id");
            pstmt.close();
            connection.close();

            String budgetName = budgetNameField.getText().trim();
            if (budgetName.isEmpty()) {
                showAlert(AlertType.ERROR, "Error", "Budget Name cannot be empty");
                return;
            }
            String category = categoryField.getValue();
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                showAlert(AlertType.ERROR, "Error", "Amount must be greater than zero");
                return;
            }
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            boolean isActive = activeCheckBox.isSelected();

            if (category.isEmpty()) {
                showAlert(AlertType.ERROR, "Error", "Category cannot be empty");
                return;
            }

            if (startDate.isAfter(endDate)) {
                showAlert(AlertType.ERROR, "Error", "Start date cannot be after end date");
                return;
            }

            Budgets newBudget = new Budgets(0, Session.getCurrentUser(), categoryId, category, budgetName, amount,
                    startDate, endDate, 0.0, 0.0, isActive, null);
            boolean result = ManageBudgetDAO.insertBudget(newBudget);

            if (result) {
                showAlert(AlertType.INFORMATION, "Success", "Budget Added Successfully!");
                initializeSampleData();
                clearForm();
                updateChartData();
                DashboardScreen.refresh();
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to Add Budget.");
            }

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Error", "Please enter a valid amount");
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Error", "Database error: " + e.getMessage());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void updateBudget() {
        Budgets selectedBudget = budgetTable.getSelectionModel().getSelectedItem();
        if (selectedBudget != null) {
            try {
                String BudgetName = budgetNameField.getText().trim();
                if (BudgetName.isEmpty()) {
                    showAlert(AlertType.ERROR, "Error", "Budget Name cannot be empty");
                    return;
                }
                /*
                 * String category = categoryField.getValue();
                 * if (category == null || category.isEmpty()) {
                 * showAlert(AlertType.ERROR,"Error", "Category cannot be empty");
                 * return;
                 * }
                 */
                double amount = Double.parseDouble(amountField.getText());
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                boolean isActive = activeCheckBox.isSelected();

                if (amount <= 0) {
                    showAlert(AlertType.ERROR, "Error", "Amount must be greater than zero");
                    return;
                }

                if (startDate.isAfter(endDate)) {
                    showAlert(AlertType.ERROR, "Error", "Start date cannot be after end date");
                    return;
                }
                selectedBudget.setBudgetName(BudgetName);
                // selectedBudget.setCategory(category);
                selectedBudget.setAmount(amount);
                boolean temp = ExpenseDAO.hasExpenseOutSideNewDateRange(startDate, endDate,
                        selectedBudget.getBudgetId(), selectedBudget.getUserId());
                if (temp == false) {
                    showAlert(AlertType.ERROR, "Error", "Expenses exist outside the new date range");
                    return;
                }
                selectedBudget.setStartDate(startDate);
                selectedBudget.setEndDate(endDate);
                selectedBudget.setIsActive(isActive);

                boolean result = ManageBudgetDAO.updateBudget(selectedBudget, selectedBudget.getUserId());

                // budgetTable.refresh();
                if (result) {
                    showAlert(AlertType.INFORMATION, "Success", "Budget updated successfully");
                    initializeSampleData();
                    updateProgressAndChart(selectedBudget);
                    updateChartData();
                    clearForm();

                } else {
                    showAlert(AlertType.ERROR, "Error", "Failed to update budget");
                }

            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Error", "Please enter a valid amount");
            }
        } else {
            showAlert(AlertType.ERROR, "Error", "Please select a budget to update");
        }
    }

    private void clearForm() {
        budgetNameField.clear();
        categoryField.setValue("Select Category");
        amountField.clear();
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusMonths(1));
        activeCheckBox.setSelected(true);
        budgetTable.getSelectionModel().clearSelection();
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void initializeSampleData() {
        List<Budgets> data = ManageBudgetDAO.getAllBudgetsForUser(Session.getCurrentUser());
        budgetList = FXCollections.observableArrayList(data);
        budgets.setAll(budgetList);
        // Sample data for testing
        /*
         * budgets.add(new Budgets("Rent", 1200.0, LocalDate.now().minusDays(2),
         * LocalDate.now().plusDays(28), true));
         * budgets.add(new Budgets("Utilities", 300.0, LocalDate.now().minusDays(3),
         * LocalDate.now().plusDays(27), true));
         * budgets.add(new Budgets("Internet", 100.0, LocalDate.now().minusDays(1),
         * LocalDate.now().plusDays(29), true));
         * budgets.add(new Budgets("Phone", 80.0, LocalDate.now(),
         * LocalDate.now().plusMonths(1), true));
         */

        /*
         * budgets.add(new Budgets("Groceries", 500.0, LocalDate.now().minusDays(5),
         * LocalDate.now().plusDays(25), true));
         * budgets.add(
         * new Budgets("Entertainment", 200.0, LocalDate.now().minusDays(10),
         * LocalDate.now().plusDays(20), true));
         * budgets.add(new Budget("Transportation", 150.0, LocalDate.now(),
         * LocalDate.now().plusMonths(1), true));
         * budgets.add(
         * new Budgets("Utilities", 300.0, LocalDate.now().minusMonths(1),
         * LocalDate.now().plusDays(15), false));
         */
        updateChartData();
    }

    private void setCategory() {
        List<Category> categories = CategoryDAO.getAllCategories(Session.getCurrentUser());
        ObservableList<String> categoryNames = FXCollections.observableArrayList();
        for (Category c : categories) {
            categoryNames.add(c.getName());
        }
        categoryField.setItems(categoryNames);
        // Replace 1 with the actual user ID

    }

    private void deleteBudget(Budgets budget) {
        if (budget == null) {
            showAlert(AlertType.ERROR, "Error", "No budget selected for deletion");
            return;
        }
        int budgetId = budget.getBudgetId();
        boolean result = ManageBudgetDAO.checkExpensesOfBudget(budgetId, budget.getUserId());
        if (result) {
            showAlert(AlertType.ERROR, "Error", "Cannot delete budget with associated expenses");
            return;
        }

        ManageBudgetDAO.deleteBudget(budgetId, Session.getCurrentUser());
        // budgets.remove(budget);

    }

}