
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.function.Function;
import java.util.function.Predicate;

public class TestSerachBarAnonymousType extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // 1. Create TableView and columns
        TableView<Person> tableView = new TableView<>();

        TableColumn<Person, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Person, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Person, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        tableView.getColumns().addAll(nameColumn, emailColumn, ageColumn);

        // 2. Create sample data
        ObservableList<Person> masterData = FXCollections.observableArrayList(
                new Person("John Doe", "john@example.com", 30),
                new Person("Jane Smith", "jane@example.com", 25),
                new Person("Bob Johnson", "bob@example.com", 40),
                new Person("Alice Brown", "alice@example.com", 35)
        );

        // 3. Create FilteredList and set to TableView
        FilteredList<Person> filteredData = new FilteredList<>(masterData, new Predicate<Person>() {
            @Override
            public boolean test(Person p) {
                return true;
            }
        });
        tableView.setItems(filteredData);

        // 4. Create search bar (TextField)
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search across all columns...");
        searchBar.setPrefWidth(300);

        // 5. Add listener to search bar for global filtering
        searchBar.textProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> obs,
                                String oldValue, String newValue) {
                filteredData.setPredicate(new Predicate<Person>() {
                    @Override
                    public boolean test(Person person) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true; // Show all when no search text
                        }

                        String lowerCaseFilter = newValue.toLowerCase();
                        return person.getName().toLowerCase().contains(lowerCaseFilter) ||
                                person.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                                String.valueOf(person.getAge()).contains(lowerCaseFilter);
                    }
                });
            }
        });

        // 6. Add context menu to columns for column-specific filtering
        addColumnFilterContextMenu(nameColumn, new Function<Person, String>() {
            @Override
            public String apply(Person person) {
                return person.getName();
            }
        });

        addColumnFilterContextMenu(emailColumn, new Function<Person, String>() {
            @Override
            public String apply(Person person) {
                return person.getEmail();
            }
        });

        addColumnFilterContextMenu(ageColumn, new Function<Person, String>() {
            @Override
            public String apply(Person person) {
                return String.valueOf(person.getAge());
            }
        });

        // 7. Create layout with search bar above table
        VBox root = new VBox(10, searchBar, tableView);
        root.setPadding(new Insets(10));

        // 8. Show stage
        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.setTitle("Combined Search Demo - Anonymous Classes");
        primaryStage.show();
    }

    // Helper method to add filter context menu to columns
    private void addColumnFilterContextMenu(TableColumn<Person, ?> column,
                                            Function<Person, String> extractor) {

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
                        FilteredList<Person> filteredList = (FilteredList<Person>) column.getTableView().getItems();
                        filteredList.setPredicate(new Predicate<Person>() {
                            @Override
                            public boolean test(Person person) {
                                if (filterText.isEmpty()) return true;
                                return extractor.apply(person).toLowerCase()
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
                FilteredList<Person> filteredList = (FilteredList<Person>) column.getTableView().getItems();
                filteredList.setPredicate(null);
            }
        });

        contextMenu.getItems().addAll(filterItem, clearFilterItem);
        column.setContextMenu(contextMenu);
    }

    // Data model class
    public static class Person {
        private final String name;
        private final String email;
        private final int age;

        public Person(String name, String email, int age) {
            this.name = name;
            this.email = email;
            this.age = age;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public int getAge() { return age; }
    }
}
