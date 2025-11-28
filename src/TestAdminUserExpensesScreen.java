

import model.User;
import dao.UserDAO;
import dao.ExpenseDAO;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.function.Function;
import java.util.function.Predicate;

public class TestAdminUserExpensesScreen extends Application {

    private TableView<User> userTable = new TableView<>();
    private ObservableList<User> userData;

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
        stage.setTitle("User Expenses");

        // Columns
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Actions column (View Expenses)
        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(createActionCellFactory());

        // Filtered list for searching
        FilteredList<User> filteredData = new FilteredList<>(userData, new Predicate<User>() {
            @Override
            public boolean test(User u) {
                return true;
            }
        });

        userTable.getColumns().addAll(nameCol, emailCol, actionsCol);
        userTable.setItems(filteredData);

        // Search Bar
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search across all columns...");
        searchBar.setPrefWidth(300);

        searchBar.textProperty().addListener((obs, oldValue, newValue) -> {
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

        Scene scene = new Scene(layout, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void addColumnFilterContextMenu(TableColumn<User, ?> column,
                                            final Function<User, String> extractor) {

        ContextMenu contextMenu = new ContextMenu();

        MenuItem filterItem = new MenuItem("Filter this column...");
        filterItem.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Filter " + column.getText());
            dialog.setHeaderText("Enter text to filter the " + column.getText() + " column:");
            dialog.showAndWait().ifPresent(filterText -> {
                FilteredList<User> filteredList = (FilteredList<User>) column.getTableView().getItems();
                filteredList.setPredicate(user -> {
                    if (filterText.isEmpty()) return true;
                    return extractor.apply(user).toLowerCase().contains(filterText.toLowerCase());
                });
            });
        });

        MenuItem clearFilterItem = new MenuItem("Clear column filter");
        clearFilterItem.setOnAction(event -> {
            FilteredList<User> filteredList = (FilteredList<User>) column.getTableView().getItems();
            filteredList.setPredicate(null);
        });

        contextMenu.getItems().addAll(filterItem, clearFilterItem);
        column.setContextMenu(contextMenu);
    }

    private Callback<TableColumn<User, Void>, TableCell<User, Void>> createActionCellFactory() {
        return new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<User, Void>() {
                    private final Button viewBtn = new Button("View Expenses");

                    {
                        viewBtn.setOnAction(event -> {
                            User selectedUser = getTableView().getItems().get(getIndex());
                            openExpenseScreen(selectedUser);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(5, viewBtn);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };
    }

    // This will open new screen for that user’s expenses
    private void openExpenseScreen(User user) {
        Stage expenseStage = new Stage();
        expenseStage.setTitle("Expenses of " + user.getName());
    
        TableView<Expense> expenseTable = new TableView<>();
    
        // Expenses load करना
        ObservableList<Expense> expenses =
                FXCollections.observableArrayList(ExpenseDAO.getExpensesByUserId(user.getId()));
    
        // Columns
        TableColumn<Expense, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
    
        TableColumn<Expense, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
    
        TableColumn<Expense, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
    
        TableColumn<Expense, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
    
        expenseTable.getColumns().addAll(categoryCol, amountCol, descCol, dateCol);
    
        // Filtered list for search
        FilteredList<Expense> filteredExpenses = new FilteredList<>(expenses, e -> true);
        expenseTable.setItems(filteredExpenses);
    
        // 🔹 Search bar (All Columns)
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search all columns...");
        searchBar.setPrefWidth(300);
    
        searchBar.textProperty().addListener((obs, oldValue, newValue) -> {
            filteredExpenses.setPredicate(exp -> {
                if (newValue == null || newValue.isEmpty()) return true;
    
                String lowerCase = newValue.toLowerCase();
                return exp.getCategory().toLowerCase().contains(lowerCase)
                        || exp.getDescription().toLowerCase().contains(lowerCase)
                        || String.valueOf(exp.getAmount()).contains(lowerCase)
                        || String.valueOf(exp.getDate()).contains(lowerCase);
            });
        });
    
        // 🔹 Column-specific filter (same as user screen)
        addExpenseColumnFilterContextMenu(categoryCol, exp -> exp.getCategory());
        addExpenseColumnFilterContextMenu(descCol, exp -> exp.getDescription());
        addExpenseColumnFilterContextMenu(dateCol, exp -> String.valueOf(exp.getDate()));
        addExpenseColumnFilterContextMenu(amountCol, exp -> String.valueOf(exp.getAmount()));
    
        // Layout
        VBox layout = new VBox(10, searchBar, expenseTable);
        layout.setPadding(new Insets(10));
        expenseStage.setScene(new Scene(layout, 600, 400));
        expenseStage.show();
    }
    
    // Column filter context menu for Expense Table
    private void addExpenseColumnFilterContextMenu(TableColumn<Expense, ?> column,
                                                   final Function<Expense, String> extractor) {
        ContextMenu contextMenu = new ContextMenu();
    
        MenuItem filterItem = new MenuItem("Filter this column...");
        filterItem.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Filter " + column.getText());
            dialog.setHeaderText("Enter text to filter the " + column.getText() + " column:");
            dialog.showAndWait().ifPresent(filterText -> {
                FilteredList<Expense> filteredList = (FilteredList<Expense>) column.getTableView().getItems();
                filteredList.setPredicate(expense -> {
                    if (filterText.isEmpty()) return true;
                    return extractor.apply(expense).toLowerCase().contains(filterText.toLowerCase());
                });
            });
        });
    
        MenuItem clearFilterItem = new MenuItem("Clear column filter");
        clearFilterItem.setOnAction(event -> {
            FilteredList<Expense> filteredList = (FilteredList<Expense>) column.getTableView().getItems();
            filteredList.setPredicate(null);
        });
    
        contextMenu.getItems().addAll(filterItem, clearFilterItem);
        column.setContextMenu(contextMenu);
    }
}