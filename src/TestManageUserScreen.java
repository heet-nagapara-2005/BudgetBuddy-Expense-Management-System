
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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

import java.time.LocalDate;
import java.util.function.Function;
import java.util.function.Predicate;

public class TestManageUserScreen extends Application {

    private TableView<User> userTable = new TableView<>();
    private ObservableList<User> userData = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initializeStaticData();
        setupUI(primaryStage);
    }

    private void initializeStaticData() {
        userData.addAll(
                new User(1, "John Doe", "john.doe@example.com", LocalDate.of(2023, 1, 15), "Active"),
                new User(2, "Jane Smith", "jane.smith@example.com", LocalDate.of(2023, 2, 20), "Active"),
                new User(3, "Robert Johnson", "robert.j@example.com", LocalDate.of(2023, 3, 10), "Inactive"),
                new User(4, "Sarah Wilson", "sarah.w@example.com", LocalDate.of(2023, 4, 5), "Active"),
                new User(5, "Michael Brown", "michael.b@example.com", LocalDate.of(2023, 5, 12), "Pending")
        );
    }

    private void setupUI(Stage stage) {
        stage.setTitle("Manage Users");

        // Columns
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, LocalDate> regDateCol = new TableColumn<>("Registration Date");
        regDateCol.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        // Actions column
        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(createActionCellFactory());

        // Filtered list for searching
        FilteredList<User> filteredData = new FilteredList<User>(userData, new Predicate<User>() {
            @Override
            public boolean test(User u) {
                return true;
            }
        });

       // userTable.getColumns().addAll(nameCol, emailCol, regDateCol, statusCol, actionsCol);
        userTable.getColumns().add(nameCol);
        userTable.getColumns().add(emailCol);
        userTable.getColumns().add(regDateCol);
        userTable.getColumns().add(actionsCol);

        userTable.setItems(filteredData);

        // Search Bar
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search across all columns...");
        searchBar.setPrefWidth(300);

        searchBar.textProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> obs,
                                String oldValue, String newValue) {
                filteredData.setPredicate(new Predicate<User>() {
                    @Override
                    public boolean test(User user) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true; // show all
                        }
                        String lowerCaseFilter = newValue.toLowerCase();
                        return user.getName().toLowerCase().contains(lowerCaseFilter)
                                || user.getEmail().toLowerCase().contains(lowerCaseFilter)
                                || String.valueOf(user.getRegistrationDate()).contains(lowerCaseFilter);
                               
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

        addColumnFilterContextMenu(regDateCol, new Function<User, String>() {
            @Override
            public String apply(User user) {
                
                return String.valueOf(user.getRegistrationDate());
            }
        });

        // Layout
        VBox layout = new VBox(10, searchBar, userTable);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 455, 450);
        stage.setScene(scene);
        stage.show();
    }

    private void addColumnFilterContextMenu(TableColumn<User, ?> column,
                                            final Function<User, String> extractor) {

        ContextMenu contextMenu = new ContextMenu();

        MenuItem filterItem = new MenuItem("Filter this column...");
        filterItem.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Filter " + column.getText());
                dialog.setHeaderText("Enter text to filter the " + column.getText() + " column:");

                dialog.showAndWait().ifPresent(new java.util.function.Consumer<String>() {
                    @Override
                    public void accept(String filterText) {
                        FilteredList<User> filteredList = (FilteredList<User>) column.getTableView().getItems();
                        filteredList.setPredicate(new Predicate<User>() {
                            @Override
                            public boolean test(User user) {
                                if (filterText.isEmpty()) return true;
                                return extractor.apply(user).toLowerCase()
                                        .contains(filterText.toLowerCase());
                            }
                        });
                    }
                });
            }
        });

        MenuItem clearFilterItem = new MenuItem("Clear column filter");
        clearFilterItem.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
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
                    private final Button deleteBtn = new Button("Delete");

                    {
                        deleteBtn.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
                            @Override
                            public void handle(javafx.event.ActionEvent event) {
                                User user = getTableView().getItems().get(getIndex());
                                deleteUser(user);
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(5, deleteBtn);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };
    }

    private void deleteUser(User user) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete User");
        confirmation.setContentText("Are you sure you want to delete user: " + user.getName() + "?");

        confirmation.showAndWait().ifPresent(new java.util.function.Consumer<ButtonType>() {
            @Override
            public void accept(ButtonType response) {
                if (response == ButtonType.OK) {
                    userData.remove(user);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully!");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class User {
        private final SimpleIntegerProperty userId;
        private final SimpleStringProperty name;
        private final SimpleStringProperty email;
        private final LocalDate registrationDate;
        private final SimpleStringProperty status;

        public User(int userId, String name, String email, LocalDate registrationDate, String status) {
            this.userId = new SimpleIntegerProperty(userId);
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
            this.registrationDate = registrationDate;
            this.status = new SimpleStringProperty(status);
        }

        public int getUserId() {
            return userId.get();
        }

        public String getName() {
            return name.get();
        }

        public String getEmail() {
            return email.get();
        }

        public LocalDate getRegistrationDate() {
            return registrationDate;
        }

        public String getStatus() {
            return status.get();
        }
    }
}
