package main;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Category;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Callback;
import javafx.scene.control.ListCell;
import utils.Session;
import java.util.List;


import dao.CategoryDAO;

public class CategoryScreen extends Application {
    public static List<Stage> categoryStage = new java.util.ArrayList<>();
    // Category fields
    private TextField categoryNameField;
   // private TextField categoryIdField;
    private ListView<Category> categoryListView;
    private Label messageLabel;

    @Override
    public void start(Stage primaryStage) {
        
        // Title Label
        Label titleLabel = new Label("Manage Categories");
        //titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        titleLabel.setId("title-label");
         // category id
       //  Label categoryIdLabel = new Label("Category Id:");
       //  categoryIdField = new TextField();
      // categoryIdField.setPromptText("Enter Category Id");
 
        // Category Name Field
        Label categoryNameLabel = new Label("Category Name:");
        categoryNameField = new TextField();
        categoryNameField.setPromptText("Enter Category Name");

        // Buttons
        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.setId("add-button"); 
        Button deleteCategoryButton = new Button("Delete Category");
        deleteCategoryButton.setId("delete-button"); 

        messageLabel = new Label();
        messageLabel.setId("message-label"); 
        // Category List (for displaying all categories)
        categoryListView = new ListView<>();
        loadCategoryList();
        categoryListView.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>() {
            @Override
            public ListCell<Category> call(ListView<Category> param) {
                return new ListCell<Category>() {
                    @Override
                    protected void updateItem(Category category, boolean empty) {
                        super.updateItem(category, empty);
                        if (empty || category == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                           // setText(category.getId() + " - " + category.getName());
                            setText(category.getName());
                        }
                    }
                };
            }
        });

        // Add Category Button Event
        addCategoryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //int categoryId = Integer.parseInt(categoryIdField.getText());
                int categoryId = 0;
                String categoryName = categoryNameField.getText();
                if (categoryName.isEmpty()) {
                    messageLabel.setText("Category name is required.");
                } else {
                    Category newCategory = new Category(categoryId,Session.getCurrentUser(),categoryName,null);
                    boolean added = CategoryDAO.addCategory(newCategory);
                    if (added) {
                        messageLabel.setText("Category added successfully.");
                        loadCategoryList();  // Refresh the list
                        categoryNameField.clear();
                        DashboardScreen.refresh();
                    } else {
                        messageLabel.setText("Failed to add category.");
                    }
                }
                
            }
            
        });

        // Delete Category Button Event
        deleteCategoryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Category selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
                if (selectedCategory != null) {
                    boolean deleted = CategoryDAO.deleteCategory(selectedCategory.getId(),Session.getCurrentUser());
                    if (deleted) {
                        messageLabel.setText("Category deleted successfully.");
                        loadCategoryList();  // Refresh the list
                        DashboardScreen.refresh();
                    } else {
                        messageLabel.setText("Failed to delete category.");
                    }
                } else {
                    messageLabel.setText("Select a category to delete.");
                }
               
            }
            
        });

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
            titleLabel,
            //categoryIdLabel,
          //  categoryIdField,
            categoryNameLabel,
            categoryNameField,
            addCategoryButton,
            deleteCategoryButton,
            categoryListView,
            messageLabel
        );

        // Scene setup
        Scene scene = new Scene(layout, 400, 500);
        String cssPath = getClass().getResource("/css/category_screen_style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Category Management");
        primaryStage.show();
        categoryStage.add(primaryStage);
    }

    // Load all categories into the list view
    private void loadCategoryList() {
        List<Category> categories = CategoryDAO.getAllCategories(Session.getCurrentUser());
        categoryListView.getItems().clear();
        categoryListView.getItems().addAll(categories);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
