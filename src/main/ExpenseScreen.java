package main;

import model.Expenses;
import model.Budgets;
import model.Category;
import database.DBConnection;
import java.sql.Connection;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.util.function.Predicate;

import java.util.function.Function;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.sql.SQLException;

import javafx.scene.control.ComboBox;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import utils.Session;

import dao.ExpenseDAO;
import dao.ManageBudgetDAO;
import dao.CategoryDAO;

public class ExpenseScreen extends Application {
    public static List<Stage> expenseStage = new java.util.ArrayList<>();
    private TableView<Expenses> expenseTable;
    private ObservableList<Expenses> expenseList;
    private DatePicker expenseDatePicker;
    private ComboBox<String> categoryComboBox, budgetComboBox;
    ObservableList<String> categoriesName;
    private TextField categoryField, amountField, descriptionField;
    private TextField searchBar;
    private FilteredList<Expenses> filteredData;

    @Override
    public void start(Stage stage) {
        Label title = new Label("Expense Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // TableView setup
        expenseTable = new TableView<>();
        setupTableColumns();
        // Setup search bar
        searchBar = new TextField();
        searchBar.setId("search-bar");
        searchBar.setPromptText("Search across all columns...");
        searchBar.setPrefWidth(300);
        // Load existing expenses
        loadExpenses();

        // Form fields
        // expenseIdField = new TextField();
        // expenseIdField.setPromptText("expense Id");

        // categoryField = new TextField();
        // categoryField.setPromptText("Category");
        categoryComboBox = new ComboBox<>();

        categoryComboBox.setPromptText("Select Category");
        categoryComboBox.setEditable(false);
        setCategoryList();

        amountField = new TextField();
        amountField.setPromptText("Amount");

        descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        expenseDatePicker = new DatePicker();
        expenseDatePicker.setPromptText("Expense Date");

        // Budget ComboBox
        budgetComboBox = new ComboBox<>();
        budgetComboBox.setPromptText("Select Budget");
        budgetComboBox.getItems().add("no Budget");

        // Load available budgets based on selected category and date
        categoryComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println("Category selected: " + categoryComboBox.getValue());
                System.out.println("Expense date selected: " + expenseDatePicker.getValue());
                if (categoryComboBox.getValue() != null && expenseDatePicker.getValue() != null) {
                    System.out.println("before method callling ");
                    loadAvailableBudgets(categoryComboBox.getValue(), expenseDatePicker.getValue());
                }
            }
        });

        expenseDatePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
                    LocalDate newValue) {
                System.out.println("Expense date changed: " + newValue);

                if (categoryComboBox.getValue() != null && newValue != null) {
                    System.out.println("Calling loadAvailableBudgets with category: " + categoryComboBox.getValue()
                            + " and date: " + newValue);
                    loadAvailableBudgets(categoryComboBox.getValue(), newValue);
                }
            }
        });

        // add listener to search bar for global filtering
        searchBar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filteredData.setPredicate(new Predicate<Expenses>() {
                    @Override
                    public boolean test(Expenses expense) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true; // Show all when no search text
                        }
                        String lowerCaseFilter = newValue.toLowerCase();
                        return expense.getCategory().toLowerCase().contains(lowerCaseFilter) ||
                                String.valueOf(expense.getAmount()).contains(lowerCaseFilter) ||
                                expense.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                                expense.getBudgetName().toLowerCase().contains(lowerCaseFilter) ||
                                expense.getE_date().toString().contains(lowerCaseFilter) ||
                                expense.getCreatedDate().toString().contains(lowerCaseFilter);
                    }
                });
            }
        });

        // Buttons
        Button addButton = new Button("Add");
        addButton.setId("add-button");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                addExpense();
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setId("delete-button");

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                deleteExpense();
            }
        });

        Button refreshButton = new Button("Refresh");
        refreshButton.setId("refresh-button");
        refreshButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                loadExpenses();
            }
        });

        Button updateButton = new Button("Update");
        updateButton.setId("update-button");
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                updateExpense();
            }
        });

        HBox formBox = new HBox(10, categoryComboBox, amountField, descriptionField, expenseDatePicker, budgetComboBox,
                addButton,
                deleteButton, refreshButton, updateButton);
        formBox.setPadding(new Insets(10));

        VBox root = new VBox(10, title, searchBar, expenseTable, formBox);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 1200, 600);

        String cssPath = getClass().getResource("/css/expense_screen_style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.setScene(scene);
        stage.setTitle("Expense Screen");
        stage.show();
        expenseStage.add(stage);
    }

    private void setupTableColumns() {
        // TableColumn<Expenses, Integer> idCol = new TableColumn<>("ID");
        // idCol.setCellValueFactory(new PropertyValueFactory<>("e_id"));

        TableColumn<Expenses, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Expenses, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Expenses, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Expenses, String> budgetCol = new TableColumn<>("Budget");
        budgetCol.setCellValueFactory(new PropertyValueFactory<>("budgetName"));

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        TableColumn<Expenses, Timestamp> dateCol = new TableColumn<>("Expense Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("e_date"));

        final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        TableColumn<Expenses, Timestamp> createdCol = new TableColumn<>("Created Date");
        createdCol.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        /*----------------------------------------------------------------- */
        /*----------------------------------------------------------------- */
        amountCol.setCellFactory(new javafx.util.Callback<TableColumn<Expenses, Double>, TableCell<Expenses, Double>>() {
                    @Override
                    public TableCell<Expenses, Double> call(TableColumn<Expenses, Double> param) {
                        return new TableCell<Expenses, Double>() {
                            @Override
                            protected void updateItem(Double amount, boolean empty) {
                                super.updateItem(amount, empty);
                                if (empty || amount == null) {
                                    setText(null);
                                } else {
                                    setText(String.format("%.2f", amount));

                                    setStyle("-fx-alignment: CENTER-RIGHT;");
                                }
                            }
                        };
                    }
                });

        dateCol.setCellFactory(new javafx.util.Callback<TableColumn<Expenses, Timestamp>, TableCell<Expenses, Timestamp>>() {
            @Override
            public TableCell<Expenses, Timestamp> call(TableColumn<Expenses, Timestamp> param) {
                return new TableCell<Expenses, Timestamp>() {
                    @Override
                    protected void updateItem(Timestamp date, boolean empty) {
                        super.updateItem(date, empty);
                        if (empty || date == null) {
                            setText(null);
                        } else {
                            // Timestamp को formatted String में बदलें
                            setText(date.toLocalDateTime().format(formatter));

                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                };
            }
        });

        createdCol.setCellFactory(new javafx.util.Callback<TableColumn<Expenses, Timestamp>, TableCell<Expenses, Timestamp>>() {
            @Override
            public TableCell<Expenses, Timestamp> call(TableColumn<Expenses, Timestamp> param) {
                return new TableCell<Expenses, Timestamp>() {
                    @Override
                    protected void updateItem(Timestamp date, boolean empty) {
                        super.updateItem(date, empty);
                        if (empty || date == null) {
                            setText(null);
                        } else {
                            setText(date.toLocalDateTime().format(formatter2));

                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                };
            }
        });

        categoryCol.setCellFactory(new javafx.util.Callback<TableColumn<Expenses, String>, TableCell<Expenses, String>>() {
            @Override
            public TableCell<Expenses, String> call(TableColumn<Expenses, String> param) {
                return new TableCell<Expenses, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle(null);
                        } else {
                            setText(item);
                            // ⭐ इनलाइन CSS से अलाइनमेंट सेट करें
                            setStyle("-fx-alignment: CENTER-LEFT;");
                        }
                    }
                };
            }
        });
        
        descCol.setCellFactory(new javafx.util.Callback<TableColumn<Expenses, String>, TableCell<Expenses, String>>() {
            @Override
            public TableCell<Expenses, String> call(TableColumn<Expenses, String> param) {
                return new TableCell<Expenses, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle(null);
                        } else {
                            setText(item);
                            // ⭐ इनलाइन CSS से अलाइनमेंट सेट करें
                            setStyle("-fx-alignment: CENTER-LEFT;");
                        }
                    }
                };
            }
        });

        budgetCol.setCellFactory(new javafx.util.Callback<TableColumn<Expenses, String>, TableCell<Expenses, String>>() {
            @Override
            public TableCell<Expenses, String> call(TableColumn<Expenses, String> param) {
                return new TableCell<Expenses, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle(null);
                        } else {
                            setText(item);
                            // ⭐ इनलाइन CSS से अलाइनमेंट सेट करें
                            setStyle("-fx-alignment: CENTER-LEFT;");
                        }
                    }
                };
            }
        });
        /*----------------------------------------------------------------- */
        /*----------------------------------------------------------------- */

        addColumnFilterContextMenu(categoryCol, new Function<Expenses, String>() {
            @Override
            public String apply(Expenses expense) {
                return expense.getCategory();
            }
        });
        addColumnFilterContextMenu(amountCol, new Function<Expenses, String>() {
            @Override
            public String apply(Expenses expense) {
                return String.valueOf(expense.getAmount());
            }
        });
        addColumnFilterContextMenu(descCol, new Function<Expenses, String>() {
            @Override
            public String apply(Expenses expense) {
                return expense.getDescription();
            }
        });
        addColumnFilterContextMenu(budgetCol, new Function<Expenses, String>() {
            @Override
            public String apply(Expenses expense) {
                return expense.getBudgetName();
            }
        });
        addColumnFilterContextMenu(dateCol, new Function<Expenses, String>() {
            @Override
            public String apply(Expenses expense) {
                return expense.getE_date().toString();
            }
        });
        addColumnFilterContextMenu(createdCol, new Function<Expenses, String>() {
            @Override
            public String apply(Expenses expense) {
                return expense.getCreatedDate().toString();
            }
        });
        // expenseTable.getColumns().add(idCol);

        expenseTable.getColumns().add(categoryCol);
        expenseTable.getColumns().add(amountCol);
        expenseTable.getColumns().add(descCol);
        expenseTable.getColumns().add(budgetCol);
        expenseTable.getColumns().add(dateCol);
        expenseTable.getColumns().add(createdCol);
        expenseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

    }

    private void loadExpenses() {
        List<Expenses> list = ExpenseDAO.getAllExpenses();
        expenseList = FXCollections.observableArrayList(list);
        filteredData = new FilteredList<>(expenseList, new Predicate<Expenses>() {
            @Override
            public boolean test(Expenses expense) {
                return true; // Initially show all expenses
            }
        });
        expenseTable.setItems(filteredData);
    }

    private void addExpense() {

        try {
            int expenseId = 0;
            int userId = Session.getCurrentUser();
            String category = categoryComboBox.getValue();
            if (category == null || category.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please select a category.");
                return;
            }

            Connection con = DBConnection.getDBConnection();

            String sql = "SELECT c_id FROM category WHERE c_name = ?;";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int categoryId = rs.getInt("c_id");

            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Amount must be greater than zero.");
                return;
            }
            String description = descriptionField.getText();
            if (description.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Description cannot be empty.");
                return;
            }

            Timestamp expenseDate;
            if (expenseDatePicker.getValue() != null) {
                expenseDate = Timestamp.valueOf(expenseDatePicker.getValue().atStartOfDay());
            } else {
                expenseDate = Timestamp.valueOf(LocalDateTime.now());
            }

            String budgetName = budgetComboBox.getValue();
            if (budgetName == null || budgetName.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please select a valid budget.");
                return;
            }
            String sql2 = "SELECT budget_id FROM budgets WHERE budget_name = ? AND user_id = ?;";
            PreparedStatement ps2 = con.prepareStatement(sql2);
            ps2.setString(1, budgetName);
            ps2.setInt(2, userId);
            ResultSet rs2 = ps2.executeQuery();
            int budgetId = 0;

            if (rs2.next()) {
                budgetId = rs2.getInt("budget_id");
            }

            Expenses expense = new Expenses(expenseId, userId, categoryId, category, amount, description, expenseDate,
                    null, budgetId, budgetComboBox.getValue());
            boolean result = ExpenseDAO.addExpense(expense);

            if (result) {
                showAlert(Alert.AlertType.INFORMATION, "Expense Added Successfully!");
                loadExpenses();

                DashboardScreen.refresh();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to Add Expense.");
            }

            ps.close();
            con.close();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid input. Please check fields.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "An error occurred: " + e.getMessage());
        }

    }

    private void deleteExpense() {
        Expenses selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean result = ExpenseDAO.deleteExpense(selected.getE_id(), Session.getCurrentUser());
            if (result) {
                showAlert(Alert.AlertType.INFORMATION, "Expense Deleted!");
                loadExpenses();
                DashboardScreen.refresh();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to delete.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select an expense.");
        }

    }

    private void updateExpense() {
        Expenses selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                int userId = Session.getCurrentUser();
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Amount must be greater than zero.");
                    return;
                }
                String category = categoryComboBox.getValue();
                if (category == null || category.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Please select a category.");
                    return;
                }

                Connection con = DBConnection.getDBConnection();

                String sql = "SELECT c_id FROM category WHERE c_name = ?;";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, category);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int categoryId = rs.getInt("c_id");

                String description = descriptionField.getText();
                if (description.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Description cannot be empty.");
                    return;
                }

                Timestamp expenseDate;
                if (expenseDatePicker.getValue() != null) {
                    expenseDate = Timestamp.valueOf(expenseDatePicker.getValue().atStartOfDay());
                } else {
                    expenseDate = selected.getE_date();
                }
                String budgetName = budgetComboBox.getValue();
                if (budgetName == null || budgetName.isEmpty() || budgetName.equals("no Budget")) {
                    showAlert(Alert.AlertType.WARNING, "Please select a valid budget.");
                    return;
                }
                String sql2 = "SELECT budget_id FROM budgets WHERE budget_name = ? AND user_id = ?;";
                PreparedStatement ps2 = con.prepareStatement(sql2);
                ps2.setString(1, budgetName);
                ps2.setInt(2, userId);
                ResultSet rs2 = ps2.executeQuery();
                int budgetId = 0;
                if (rs2.next()) {
                    budgetId = rs2.getInt("budget_id");
                }

                Expenses updatedExpense = new Expenses(selected.getE_id(), Session.getCurrentUser(), categoryId,
                        category, amount, description, expenseDate, selected.getCreatedDate(), budgetId,
                        budgetComboBox.getValue());
                boolean result = ExpenseDAO.updateExpense(updatedExpense, Session.getCurrentUser());

                if (result) {
                    showAlert(Alert.AlertType.INFORMATION, "Expense Updated Successfully!");
                    loadExpenses();
                    DashboardScreen.refresh();
                    clearFields();

                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to Update Expense.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Invalid input. Please check fields.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "An error occurred: " + e.getMessage());
            }

        } else {
            showAlert(Alert.AlertType.WARNING, "Please select an expense.");
        }

    }

    private void setCategoryList() {

        List<Category> categories = CategoryDAO.getAllCategories(Session.getCurrentUser());
        categoriesName = FXCollections.observableArrayList();

        for (Category category : categories) {
            categoriesName.add(category.getName());
            categoryComboBox.setItems(categoriesName);
        }

    }

    private void loadAvailableBudgets(String category, LocalDate expenseDate) {

        List<Budgets> budgets = ManageBudgetDAO.currentMatchBudgets(category, expenseDate, Session.getCurrentUser());
        ObservableList<String> budgetNames = FXCollections.observableArrayList();
        budgetNames.add("no Budget"); // Add default option
        for (Budgets budget : budgets) {
            budgetNames.add(budget.getBudgetName());
        }
        budgetComboBox.setItems(budgetNames);
    }

    private void clearFields() {
        // expenseIdField.clear();
        categoryField.clear();
        amountField.clear();
        descriptionField.clear();
        expenseDatePicker.setValue(null);
        budgetComboBox.getItems().clear();
        budgetComboBox.setPromptText("Select Budget");
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void addColumnFilterContextMenu(TableColumn<Expenses, ?> column,
            Function<Expenses, String> extractor) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem filterItem = new MenuItem("Filter this column...");

        filterItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Filter " + column.getText());
                dialog.setHeaderText("Enter text to filter the " + column.getText() + " column:");

                dialog.showAndWait().ifPresent(new java.util.function.Consumer<String>() {

                    @Override
                    public void accept(String filterText) {
                        filteredData.setPredicate(new Predicate<Expenses>() {
                            @Override
                            public boolean test(Expenses expense) {
                                if (filterText == null || filterText.isEmpty())
                                    return true;
                                return extractor.apply(expense).toLowerCase()
                                        .contains(filterText.toLowerCase());
                            }
                        });
                    }
                });
            }
        });
        MenuItem clearFilterItem = new MenuItem("Clear column filter");
        clearFilterItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FilteredList<Expenses> filteredData = (FilteredList<Expenses>) expenseTable.getItems();
                filteredData.setPredicate(null); // Clear filter
            }
        });
        contextMenu.getItems().addAll(filterItem, clearFilterItem);
        column.setContextMenu(contextMenu);
    }

    public static void main(String[] args) {
        DBConnection.getDBConnection(); // Ensure DB connection initializes
        launch();
    }
}
