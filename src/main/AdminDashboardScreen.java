package main;

import dao.AdminDAO;

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

public class AdminDashboardScreen extends Application {

    private BorderPane rootLayout;
    private static Label totalUserLabel;

    @Override
    public void start(Stage primaryStage) {
        // Title label
        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.getStyleClass().add("title-label");

        // Top bar layout
        HBox topBar = new HBox(titleLabel);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(15));
        topBar.getStyleClass().add("top-bar");

        // Navigation buttons

        Button manageUserBtn = new Button("Manage User");
        manageUserBtn.setPrefWidth(150);
        Button userExpenseBtn = new Button("User Expenses");
        userExpenseBtn.setPrefWidth(150);
        Button userBudgetBtn = new Button("User Budgets ");
        userBudgetBtn.setPrefWidth(150);
        Button userExpenseAnalysisBtn = new Button("Analysis");
        userExpenseAnalysisBtn.setPrefWidth(150);
        Button adminProfileScreenBtn = new Button("Profile");
        adminProfileScreenBtn.setPrefWidth(150);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setPrefWidth(150);
        // Adding interactive CSS for buttons with a better color scheme
        manageUserBtn.getStyleClass().add("nav-button");
        userExpenseBtn.getStyleClass().add("nav-button");
        userBudgetBtn.getStyleClass().add("nav-button");
        userExpenseAnalysisBtn.getStyleClass().add("nav-button");

        adminProfileScreenBtn.getStyleClass().add("nav-button");
        logoutBtn.getStyleClass().add("nav-button");

        VBox navBar = new VBox(15, manageUserBtn, userExpenseBtn, userBudgetBtn, userExpenseAnalysisBtn,
                adminProfileScreenBtn, logoutBtn);
        navBar.setAlignment(Pos.TOP_LEFT);
        navBar.setPadding(new Insets(20));
        navBar.getStyleClass().add("nav-bar");

        // Center content placeholder
        Label welcomeLabel = new Label("Welcome to Admin Dashboard!");
        welcomeLabel.getStyleClass().add("welcome-label");

        totalUserLabel = new Label();
        totalUserLabel.getStyleClass().add("stat-label");

        VBox contentArea = new VBox(15, welcomeLabel, totalUserLabel);
        updateDashboardData();
        // Root layout
        rootLayout = new BorderPane();
        rootLayout.setTop(topBar);
        rootLayout.setLeft(navBar);
        rootLayout.setCenter(contentArea);

        BorderPane.setMargin(contentArea, new Insets(20));

        rootLayout.getStyleClass().add("root");

        manageUserBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                openManageUserScreen(primaryStage);
            }
        });

        userExpenseBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                openUserExpensesScreen(primaryStage);
            }
        });

        userBudgetBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                openUserBudgetScreen(primaryStage);

            }

        });

        userExpenseAnalysisBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                openUserExpenseAnalysisScreen(primaryStage);

            }
        });

        adminProfileScreenBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                openAdminProfileScreen(primaryStage);

            }
        });

        logoutBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                primaryStage.close();
                for (Stage stage : AdminManageUserScreen.adminManageUserStage) {
                    stage.close();
                }
                for (Stage stage : AdminUserExpensesScreen.adminUserExpensesStage) {
                    stage.close();
                }
                for (Stage stage : AdminUserBudgetScreen.adminUserBudgetStage) {
                    stage.close();
                }
                for (Stage stage : AdminAnalysisScreen.adminAnalysisStage) {
                    stage.close();
                }
                for (Stage stage : AdminProfileScreen.adminProfileStage) {
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
    }

    private static void updateDashboardData() {
        int totalCategories = AdminDAO.totalUsers();

        totalUserLabel.setText("Total Registered User :" + totalCategories);

    }

    public static void refresh() {
        updateDashboardData(); // This will refresh the dashboard data after each button click.
    }

    // Screens ke placeholder methods
    private void openManageUserScreen(Stage primaryStage) {
        AdminManageUserScreen ManageUserScreen = new AdminManageUserScreen();
        ManageUserScreen.start(new Stage());
    }

    private void openUserExpensesScreen(Stage primaryStage) {
        AdminUserExpensesScreen userExpensesScreen = new AdminUserExpensesScreen();
        userExpensesScreen.start(new Stage());
    }

    private void openUserBudgetScreen(Stage primaryStage) {
        AdminUserBudgetScreen userbudgetScreen = new AdminUserBudgetScreen();
        userbudgetScreen.start(new Stage());
    }

    private void openUserExpenseAnalysisScreen(Stage primaryStage) {
        AdminAnalysisScreen AnalysisScreen = new AdminAnalysisScreen();
        AnalysisScreen.start(new Stage());
    }

    private void openAdminProfileScreen(Stage primaryStage) {
        AdminProfileScreen adminProfileScreen = new AdminProfileScreen();
        adminProfileScreen.start(new Stage());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
