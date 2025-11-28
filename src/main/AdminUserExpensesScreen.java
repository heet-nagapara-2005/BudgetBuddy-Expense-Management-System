package main;

import model.Expenses;
import model.User;
import dao.UserDAO;
import dao.ExpenseDAO;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class AdminUserExpensesScreen extends Application {
    public static List<Stage> adminUserExpensesStage = new java.util.ArrayList<>();
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
        /*------------------------------------------------------------------------------------------------- */
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
        /*------------------------------------------------------------------------------------------------- */

        // Filtered list for searching
        FilteredList<User> filteredData = new FilteredList<>(userData, new Predicate<User>() {
            @Override
            public boolean test(User u) {
                return true;
            }
        });

        // userTable.getColumns().addAll(nameCol, emailCol, actionsCol);
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
        scene.getStylesheets()
                .add(getClass().getResource("/css/admin_user_expenses_screen_style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
        adminUserExpensesStage.add(stage);
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
                    private final Button viewBtn = new Button("View Expenses");

                    {
                        viewBtn.getStyleClass().addAll("button", "view-button"); // 👈 Add this
                        viewBtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                User selectedUser = getTableView().getItems().get(getIndex());
                                openExpenseScreen(selectedUser);
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

    // This will open new screen for that user’s expenses
    private void openExpenseScreen(User user) {
        Stage expenseStage = new Stage();
        expenseStage.setTitle("Expenses of " + user.getName());

        TableView<Expenses> expenseTable = new TableView<>();

        // Expenses load करना
        ObservableList<Expenses> expenses = FXCollections
                .observableArrayList(ExpenseDAO.getExpensesByUserId(user.getId()));

        // Columns
        TableColumn<Expenses, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Expenses, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Expenses, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


        TableColumn<Expenses, Timestamp> expenseDateCol = new TableColumn<>("Expense Date");
        expenseDateCol.setCellValueFactory(new PropertyValueFactory<>("e_date"));

        TableColumn<Expenses, String> budgetCol = new TableColumn<>("Budget Name");
        budgetCol.setCellValueFactory(new PropertyValueFactory<>("budgetName"));

        final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");


        TableColumn<Expenses, Timestamp> CreatedDateCol = new TableColumn<>("Created Date");
        CreatedDateCol.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        /*--------------------------------------------------------------------------------------- */
        amountCol
                .setCellFactory(new javafx.util.Callback<TableColumn<Expenses, Double>, TableCell<Expenses, Double>>() {
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

        expenseDateCol.setCellFactory(
                new javafx.util.Callback<TableColumn<Expenses, Timestamp>, TableCell<Expenses, Timestamp>>() {
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

        CreatedDateCol.setCellFactory(
                new Callback<TableColumn<Expenses, Timestamp>, TableCell<Expenses, Timestamp>>() {
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

        categoryCol
                .setCellFactory(new javafx.util.Callback<TableColumn<Expenses, String>, TableCell<Expenses, String>>() {
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

        budgetCol
                .setCellFactory(new javafx.util.Callback<TableColumn<Expenses, String>, TableCell<Expenses, String>>() {
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
        /*------------------------------------------------------------------------------------------ */

        // expenseTable.getColumns().addAll(categoryCol, amountCol, descCol, dateCol);
        expenseTable.getColumns().add(categoryCol);
        expenseTable.getColumns().add(amountCol);
        expenseTable.getColumns().add(descCol);

        expenseTable.getColumns().add(expenseDateCol);
        expenseTable.getColumns().add(budgetCol);
        expenseTable.getColumns().add(CreatedDateCol);
        expenseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Filtered list for search
        FilteredList<Expenses> filteredExpenses = new FilteredList<>(expenses, new Predicate<Expenses>() {
            @Override
            public boolean test(Expenses e) {
                return true;
            }
        });
        expenseTable.setItems(filteredExpenses);
        // 🔹 Search bar (All Columns)
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search all columns...");
        searchBar.setPrefWidth(300);

        searchBar.textProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String oldValue,
                    String newValue) {
                filteredExpenses.setPredicate(new java.util.function.Predicate<Expenses>() {
                    @Override
                    public boolean test(Expenses exp) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String lowerCase = newValue.toLowerCase();
                        // Assuming getE_date() is the correct getter for the expense date,
                        // as getDate() was flagged as undefined in the lint context.
                        return exp.getCategory().toLowerCase().contains(lowerCase)
                                || exp.getDescription().toLowerCase().contains(lowerCase)
                                || String.valueOf(exp.getAmount()).toLowerCase().contains(lowerCase)
                                || String.valueOf(exp.getE_date()).toLowerCase().contains(lowerCase)
                                || String.valueOf(exp.getBudgetName()).toLowerCase().contains(lowerCase)
                                || String.valueOf(exp.getCreatedDate()).toLowerCase().contains(lowerCase);
                    }
                });
            }
        });

        // 🔹 Column-specific filter (same as user screen)
        addExpenseColumnFilterContextMenu(categoryCol, new java.util.function.Function<model.Expenses, String>() {
            @Override
            public String apply(model.Expenses exp) {
                return exp.getCategory();
            }
        });
        addExpenseColumnFilterContextMenu(descCol, new java.util.function.Function<model.Expenses, String>() {
            @Override
            public String apply(model.Expenses exp) {
                return exp.getDescription();
            }
        });
        addExpenseColumnFilterContextMenu(expenseDateCol, new java.util.function.Function<model.Expenses, String>() {
            @Override
            public String apply(model.Expenses exp) {
                // Using getE_date() as per context, assuming it's the correct date getter
                return String.valueOf(exp.getE_date());
            }
        });
        addExpenseColumnFilterContextMenu(amountCol, new java.util.function.Function<model.Expenses, String>() {
            @Override
            public String apply(model.Expenses exp) {
                return String.valueOf(exp.getAmount());
            }
        });
        addExpenseColumnFilterContextMenu(budgetCol, new java.util.function.Function<model.Expenses, String>() {
            @Override
            public String apply(model.Expenses exp) {
                return String.valueOf(exp.getBudgetName());
            }
        });
        addExpenseColumnFilterContextMenu(CreatedDateCol, new java.util.function.Function<model.Expenses, String>() {
            @Override
            public String apply(model.Expenses exp) {
                return String.valueOf(exp.getCreatedDate());
            }
        });

        // Layout
        VBox layout = new VBox(10, searchBar, expenseTable);
        layout.setPadding(new Insets(10));
        Scene scene = new Scene(layout, 780, 400);
        scene.getStylesheets()
                .add(getClass().getResource("/css/admin_user_expenses_screen_style.css").toExternalForm());

        expenseStage.setScene(scene);
        expenseStage.show();
    }

    // Column filter context menu for Expense Table
    private void addExpenseColumnFilterContextMenu(TableColumn<Expenses, ?> column,
            final Function<Expenses, String> extractor) {
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
                        FilteredList<Expenses> filteredList = (FilteredList<Expenses>) column.getTableView().getItems();
                        filteredList.setPredicate(new Predicate<Expenses>() {
                            @Override
                            public boolean test(Expenses expense) {
                                if (filterText.isEmpty())
                                    return true;
                                return extractor.apply(expense).toLowerCase().contains(filterText.toLowerCase());
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
                FilteredList<Expenses> filteredList = (FilteredList<Expenses>) column.getTableView().getItems();
                filteredList.setPredicate(null);
            }
        });

        contextMenu.getItems().addAll(filterItem, clearFilterItem);
        column.setContextMenu(contextMenu);
    }
}
