
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class TestContextMenuSearch2 extends Application {

    public static class Person {
        private final String name;
        private final int age;
        private final LocalDate birthDate;

        public Person(String name, int age, LocalDate birthDate) {
            this.name = name;
            this.age = age;
            this.birthDate = birthDate;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
        public LocalDate getBirthDate() { return birthDate; }
    }

    private FilteredList<Person> filteredData;
    // Store active predicate for each column id (key = columnId)
    private final Map<String, Predicate<Person>> columnPredicates = new HashMap<>();

    @Override
    public void start(Stage stage) {
        ObservableList<Person> masterData = FXCollections.observableArrayList(
            new Person("Alice", 25, LocalDate.of(1999, 5, 10)),
            new Person("Bob", 30, LocalDate.of(1994, 3, 22)),
            new Person("Charlie", 22, LocalDate.of(2002, 1, 15)),
            new Person("David", 28, LocalDate.of(1996, 8, 5)),
            new Person("Eve", 40, LocalDate.of(1984, 12, 1))
        );

        filteredData = new FilteredList<>(masterData, p -> true);

        TableView<Person> tableView = new TableView<>();
        tableView.setItems(filteredData);

        TableColumn<Person, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setId("name"); // id used as key in predicates map
        addHeaderSearch(nameCol, "name");

        TableColumn<Person, Integer> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        ageCol.setId("age");
        addHeaderSearch(ageCol, "age");

        TableColumn<Person, LocalDate> dateCol = new TableColumn<>("Birth Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        dateCol.setId("birthDate");
        addHeaderSearch(dateCol, "birthDate");

        tableView.getColumns().addAll(nameCol, ageCol, dateCol);

        VBox root = new VBox(8, tableView);
        root.setPrefSize(600, 400);

        stage.setScene(new Scene(root));
        stage.setTitle("TableView Header Context Search Example");
        stage.show();
    }

    /**
     * Adds a context-menu search to the header area of the given TableColumn.
     * columnKey: unique key to identify the column in the columnPredicates map.
     */
    private <T> void addHeaderSearch(TableColumn<Person, T> column, String columnKey) {
        // Create a label to use as header graphic so we can attach a context menu to it
        Label headerLabel = new Label(column.getText());
        headerLabel.setStyle("-fx-padding: 4 6 4 6; -fx-cursor: hand;"); // small padding + hand cursor
        column.setGraphic(headerLabel);

        // Build context menu
        ContextMenu cm = new ContextMenu();

        // TextField inside a CustomMenuItem so user can type
        TextField tf = new TextField();
        tf.setPromptText("Search " + column.getText());

        // Allow CustomMenuItem to stay open while typing: hideOnClick = false
        CustomMenuItem tfItem = new CustomMenuItem(tf, false);

        MenuItem apply = new MenuItem("Apply");
        MenuItem clear = new MenuItem("Clear");
        MenuItem close = new MenuItem("Close");

        cm.getItems().addAll(tfItem, new SeparatorMenuItem(), apply, clear, close);

        // Apply action (also invoked by Enter key)
        apply.setOnAction(e -> {
            String input = tf.getText();
            if (input == null || input.trim().isEmpty()) {
                columnPredicates.remove(columnKey);
            } else {
                final String normalized = input.trim().toLowerCase();
                // create predicate depending on columnKey
                Predicate<Person> predicate = createColumnPredicate(columnKey, normalized);
                columnPredicates.put(columnKey, predicate);
            }
            updateCombinedFilter();
            cm.hide();
        });

        // Clear action: remove any filter for this column
        clear.setOnAction(e -> {
            tf.clear();
            columnPredicates.remove(columnKey);
            updateCombinedFilter();
            cm.hide();
        });

        close.setOnAction(e -> cm.hide());

        // Enter key on textfield triggers Apply
        tf.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                apply.fire();
            } else if (ev.getCode() == KeyCode.ESCAPE) {
                cm.hide();
            }
        });

        // Show context menu on right-click of the header label
        headerLabel.setOnContextMenuRequested(evt -> {
            // If there is an existing filter for this column, prefill the textfield
            Predicate<Person> existing = columnPredicates.get(columnKey);
            if (existing != null) {
                // best-effort: we cannot extract the original search string from predicate,
                // but if you store the raw string along with predicate you can prefill it.
                // For simplicity, we leave tf as-is (or you can store raw strings in another map).
            } else {
                tf.clear();
            }
            cm.show(headerLabel, evt.getScreenX(), evt.getScreenY());
            evt.consume();
        });

        // Also show the menu on left-click (optional)
        headerLabel.setOnMouseClicked(ev -> {
            if (ev.isStillSincePress() && ev.getClickCount() == 1 && ev.isPrimaryButtonDown()) {
                cm.show(headerLabel, ev.getScreenX(), ev.getScreenY());
            }
        });
    }

    // Create a predicate for a columnKey and normalized (lowercase) search term
    private Predicate<Person> createColumnPredicate(String columnKey, String normalizedSearch) {
        switch (columnKey) {
            case "name":
                return person -> {
                    String v = person.getName();
                    return v != null && v.toLowerCase().contains(normalizedSearch);
                };
            case "age":
                return person -> String.valueOf(person.getAge()).contains(normalizedSearch);
            case "birthDate":
            case "birthdate": // be flexible
                return person -> {
                    LocalDate d = person.getBirthDate();
                    return d != null && d.toString().toLowerCase().contains(normalizedSearch);
                };
            default:
                // default fallback: no filtering
                return person -> true;
        }
    }

    // Combine all active column predicates (AND) into a single predicate and set on filteredData
    private void updateCombinedFilter() {
        if (columnPredicates.isEmpty()) {
            filteredData.setPredicate(p -> true);
        } else {
            filteredData.setPredicate(person -> {
                for (Predicate<Person> pred : columnPredicates.values()) {
                    if (!pred.test(person)) {
                        return false; // one column filter failed → exclude row
                    }
                }
                return true; // all column filters passed → include row
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
