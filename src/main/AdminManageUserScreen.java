package main;
import model.User;
import dao.*;

import javafx.application.Application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class AdminManageUserScreen extends Application {
    public static List<Stage> adminManageUserStage = new java.util.ArrayList<>();
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
        stage.setTitle("Manage Users");

        userTable.setId("userTable"); 


        // Columns
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, Timestamp> regDateCol = new TableColumn<>("Registration Date");
        regDateCol.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        // Actions column
        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(createActionCellFactory());

        /*----------------------------------------------------------------- */
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
                        //setAlignment(Pos.CENTER_LEFT); 
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
                            //setAlignment(Pos.CENTER_LEFT); 
                            setStyle("-fx-alignment: center-left ;"); 

                        }
                    }
                };
            }
        });

        regDateCol.setCellFactory(new Callback<TableColumn<User, Timestamp>, TableCell<User, Timestamp>>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
            @Override
            public TableCell<User, Timestamp> call(TableColumn<User, Timestamp> param) {
                return new TableCell<User, Timestamp>() {
                    @Override
                    protected void updateItem(Timestamp item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            LocalDateTime dateTime = item.toLocalDateTime();
                            setText(dateTime.format(formatter)); 
                            setStyle("-fx-alignment: center ;"); 

                            //setAlignment(Pos.CENTER); 
                        }
                    }
                };
            }
        });

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
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        userTable.setItems(filteredData);

        // Search Bar
        TextField searchBar = new TextField();
        searchBar.getStyleClass().add("text-field");
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
                                || String.valueOf(user.getCreatedDate()).contains(lowerCaseFilter);
                               
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
                
                return String.valueOf(user.getCreatedDate());
            }
        });

        // Layout
        VBox layout = new VBox(10, searchBar, userTable);
        layout.setPadding(new Insets(10));
        layout.getStyleClass().add("root"); 
        
        actionsCol.getStyleClass().add("actions-cell");
        Scene scene = new Scene(layout, 600, 450);
        scene.getStylesheets().add(getClass().getResource("/css/admin_manage_user_screen_style.css").toExternalForm()); 

        stage.setScene(scene);
        stage.show();
        adminManageUserStage.add(stage);
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
                        deleteBtn.getStyleClass().addAll("button", "delete-button"); // <-- CLASSES ADDED

                        deleteBtn.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
                            @Override
                            public void handle(javafx.event.ActionEvent event) {
                                User selectedUser = getTableView().getItems().get(getIndex());
                                deleteUser(selectedUser);
                                
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
                            buttons.setAlignment(Pos.CENTER); 
                            setGraphic(buttons);
                            setAlignment(Pos.CENTER);
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
                    if(UserDAO.deleteUserAndAllData(user.getId())){
                        
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully!");
                    initializeData();
                    AdminDashboardScreen.refresh();

                    }
                    else{
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user.");
                    }
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

   
}
