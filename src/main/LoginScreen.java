package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import utils.Session;
import dao.*;
import database.DBConnection;

public class LoginScreen extends Application {
    public static List<Stage> loginStage = new java.util.ArrayList<>();
    public static Stage openLoginStage;
    public void start(Stage primaryStage) {
        openLoginStage = primaryStage;
        // --- UI Elements ---
        Label titleLabel = new Label("Expense Manager");
        titleLabel.getStyleClass().add("login-title"); 
        Label userNameLabel = new Label("Username:");
        userNameLabel.getStyleClass().add("form-label"); 
        TextField userNameTextField = new TextField();
        userNameTextField.setPromptText("Enter your username");

        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("form-label"); 
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button"); 

        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("message-label"); 

        Hyperlink signUpLink = new Hyperlink("Don't have an account? Sign Up here.");
        signUpLink.getStyleClass().add("signup-link"); 

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (AdminDAO.isAdmin(userNameTextField.getText(), passwordField.getText())) {
                    messageLabel.setText("Admin login successful!");
                    messageLabel.getStyleClass().setAll("message-label", "success"); 
                    AdminDashboardScreen adminDashboard = new AdminDashboardScreen();
                    adminDashboard.start(new Stage());
                    for (Stage stage : loginStage) {
                        stage.close();
                    }

                } else if (validUser(userNameTextField.getText(),  passwordField.getText()) ){
                    messageLabel.setText("Login successful!");
                    messageLabel.getStyleClass().setAll("message-label", "success"); 
                    DashboardScreen dashboard = new DashboardScreen();
                    dashboard.start(new Stage());
                    for (Stage stage : loginStage) {
                        stage.close();
                    }
                    // for(Stage stage : SignUpScreen.signupStage) { stage.close(); }
                    // Main.mainStage.close();
                } else {
                    messageLabel.setText("Invalid username or password!");
                    messageLabel.getStyleClass().setAll("message-label", "error"); 
                    userNameTextField.clear();
                    passwordField.clear();
                }
            }
        });

        signUpLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                SignUpScreen signUpScreen = new SignUpScreen();
                try {
                    primaryStage.close();
                    signUpScreen.start(new Stage());
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        });

        GridPane grid = new GridPane();
        grid.getStyleClass().add("login-grid"); 
        grid.setVgap(10);
        grid.setHgap(10);

        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));
        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(userNameLabel, 0, 1);
        grid.add(userNameTextField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(loginButton, 1, 3);
        grid.add(messageLabel, 0, 4, 2, 1); 
        grid.add(signUpLink, 0, 5, 2, 1);
        GridPane.setHalignment(signUpLink, javafx.geometry.HPos.CENTER);

        GridPane.setHalignment(messageLabel, javafx.geometry.HPos.CENTER);

    
        Scene scene = new Scene(grid, 400, 300);
        
        scene.getStylesheets().add(getClass().getResource("/css/login_screen_style.css").toExternalForm());

        primaryStage.setTitle("Login Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
        loginStage.add(primaryStage);
    }

    private static boolean validUser(String u_name, String password) {
        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "SELECT * FROM users WHERE username =?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, u_name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if(BCrypt.checkpw(password, rs.getString("password"))) {
                Session.setCurrentUser(rs.getInt("id"));
                return true;
                }
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}