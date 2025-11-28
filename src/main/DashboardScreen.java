package main;

import java.util.List;

import dao.CategoryDAO;
import dao.ExpenseDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.stage.Stage;
import utils.Session;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class DashboardScreen extends Application {
    public static List<Stage> dashboardStage = new java.util.ArrayList<>();

    private BorderPane rootLayout;
    private static Label categoryLabel, expenseLabel, amountSpentLabel;

    @Override
    public void start(Stage primaryStage) {
        // Title label
        Label titleLabel = new Label("Expense Manager - Dashboard");
        titleLabel.getStyleClass().add("title-label");

        // Top bar layout
        HBox topBar = new HBox(titleLabel);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(15));
        topBar.getStyleClass().add("top-bar");

        // Navigation buttons

        Button expenseBtn = new Button("Expenses");
        expenseBtn.setPrefWidth(150);
        Button categoryBtn = new Button("Categories");
        categoryBtn.setPrefWidth(150);
        Button budgetBtn = new Button("Manage Budgets");
        budgetBtn.setPrefWidth(150);
        Button expenseAnayticsBtn = new Button("Expense Analytics");
        expenseAnayticsBtn.setPrefWidth(150);

        Button profileScreenBtn = new Button("Profile");
        profileScreenBtn.setPrefWidth(150);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setPrefWidth(150);
        // Adding interactive CSS for buttons with a better color scheme
        expenseBtn.getStyleClass().add("nav-button");
        categoryBtn.getStyleClass().add("nav-button");
        budgetBtn.getStyleClass().add("nav-button");
        expenseAnayticsBtn.getStyleClass().add("nav-button");
        profileScreenBtn.getStyleClass().add("nav-button");
        logoutBtn.getStyleClass().add("nav-button");

        VBox navBar = new VBox(15, categoryBtn, budgetBtn, expenseBtn, expenseAnayticsBtn, profileScreenBtn, logoutBtn);
        navBar.setAlignment(Pos.TOP_LEFT);
        navBar.setPadding(new Insets(20));
        navBar.getStyleClass().add("nav-bar");

        // Center content placeholder
        Label welcomeLabel = new Label("Welcome to Expense Manager!");
        welcomeLabel.getStyleClass().add("welcome-label");

        categoryLabel = new Label();
        categoryLabel.getStyleClass().add("stat-label");
        expenseLabel = new Label();
        expenseLabel.getStyleClass().add("stat-label");
        amountSpentLabel = new Label();
        amountSpentLabel.getStyleClass().add("stat-label");
        VBox contentArea = new VBox(15, welcomeLabel, categoryLabel, expenseLabel, amountSpentLabel);
        updateDashboardData();

        // Root layout
        rootLayout = new BorderPane();
        rootLayout.setTop(topBar);
        rootLayout.setLeft(navBar);
        rootLayout.setCenter(contentArea);

        BorderPane.setMargin(contentArea, new Insets(20));
        // Button actions
        rootLayout.getStyleClass().add("root");

        expenseBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                openExpensesScreen(primaryStage);
            }
        });
        categoryBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                openCategoriesScreen(primaryStage);
            }
        });
        budgetBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                openBudgetScreen(primaryStage);

            }

        });

        expenseAnayticsBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                openExpenseAnalysisScreen(primaryStage);

            }
        });

        profileScreenBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //System.out.println("before  profile screen");
                openUserProfileScreen(primaryStage);
                //System.out.println("after  profile screen");
            }
        });

        logoutBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                primaryStage.close();
                Session.clearSession();
                for (Stage stage : CategoryScreen.categoryStage) {
                    stage.close();
                }
                for( Stage stage : ManageBudgetScreen.budgetStage) {
                    stage.close();
                }
                for( Stage stage : ExpenseScreen.expenseStage) {
                    stage.close();
                }
                for( Stage stage : ExpenseAnalysisScreen.expenseAnalysisStage) {
                    stage.close();
                }
                for( Stage stage : UserProfileScreen.profileStage) {
                    stage.close();
                }
              Session.clearSession();

              LoginScreen.openLoginStage.show();                
            }
        });

        // Scene setup
        Scene scene = new Scene(rootLayout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/dashboard_style.css").toExternalForm());

        primaryStage.setTitle("Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
        dashboardStage.add(primaryStage);
    }

    private static void updateDashboardData() {
        int totalCategories = CategoryDAO.totalCategories(Session.getCurrentUser());
        int totalExpenses = ExpenseDAO.totalExpenses(Session.getCurrentUser());
        double totalAmountSpent = ExpenseDAO.totalAmountSpent(Session.getCurrentUser());

        categoryLabel.setText("Total Categories is :" + totalCategories);
        expenseLabel.setText("Total Expenses is :" + totalExpenses);
        amountSpentLabel.setText("Total Amount Spent is :" + totalAmountSpent);
        // For testing purpose
        // updateDashboardData(); // Uncomment this line after testing the data fetch
        // function.

    }

    public static void refresh() {
        updateDashboardData(); // This will refresh the dashboard data after each button click.
    }

    // Screens ke placeholder methods
    private void openExpensesScreen(Stage primaryStage) {
        ExpenseScreen expenseScreen = new ExpenseScreen();
        expenseScreen.start(new Stage());
    }

    private void openCategoriesScreen(Stage primaryStage) {
        CategoryScreen categoriesScreen = new CategoryScreen();
        categoriesScreen.start(new Stage());
    }

    private void openBudgetScreen(Stage primaryStage) {
        ManageBudgetScreen budgetScreen = new ManageBudgetScreen();
        budgetScreen.start(new Stage());
    }

    private void openExpenseAnalysisScreen(Stage primaryStage) {
        ExpenseAnalysisScreen expenseAnalysisScreen = new ExpenseAnalysisScreen();
        expenseAnalysisScreen.start(new Stage());
    }

    private void openUserProfileScreen(Stage primaryStage) {
        UserProfileScreen profileScreen = new UserProfileScreen();
        profileScreen.start(new Stage());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
