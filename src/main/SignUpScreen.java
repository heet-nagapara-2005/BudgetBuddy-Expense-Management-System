package main;

import java.util.List;

import dao.UserDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.User;
import utils.EmailUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


public class SignUpScreen extends Application {
    public static List<Stage> signupStage = new java.util.ArrayList<>();
 
    @Override
    public void start(Stage primaryStage) {
        
        primaryStage.setTitle("Signup Screen");
        BorderPane root = new BorderPane();
        root.getStyleClass().add("signup-root"); 
        root.setPadding(new Insets(20));

        Label titleLabel = new Label("Sign Up");
        titleLabel.getStyleClass().add("signup-title"); 

        HBox topBar = new HBox(titleLabel);
        topBar.getStyleClass().add("top-bar"); 
        topBar.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Username:");
        nameLabel.getStyleClass().add("form-label"); 
        TextField nameField = new TextField();

        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("form-label"); 
        TextField emailField = new TextField();

        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("form-label"); 
        PasswordField passwordField = new PasswordField();

        Button signupButton = new Button("Sign Up");
        signupButton.getStyleClass().add("signup-button"); 
        
        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("message-label"); 
        
        Hyperlink loginLink = new Hyperlink("Already have an account? Login here.");
        loginLink.getStyleClass().add("login-link"); 


        signupButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    String email = emailField.getText();
                    String password = passwordField.getText();

                    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        messageLabel.setText("Please fill all fields!");
                        messageLabel.getStyleClass().setAll("message-label", "error"); 
                        return;
                    }
                    
                    if (!EmailUtil.isValidEmail(email)) {
                        messageLabel.setText("Invalid email format!");
                        messageLabel.getStyleClass().setAll("message-label", "error"); 
                        return;
                    }
                    if(password.length() > 8){
                        messageLabel.setText("Password should be at most 8 characters!");
                        messageLabel.getStyleClass().setAll("message-label", "error"); 
                        return;
                    } 

                    User user = new User(0, name, email, password, null);
                    boolean result = UserDAO.addUser(user);

                    if (result) {
                        messageLabel.setText("Signup successful!");
                        messageLabel.getStyleClass().setAll("message-label", "success"); 
                        EmailUtil.sendMail(email,"budgetbuddy6353@gmail.com", "Welcome to Expense Manager", "Thank you for signing up, " + name + "!");

                        nameField.clear();
                        emailField.clear();
                        passwordField.clear();
                        
                        primaryStage.close();
                        LoginScreen loginScreen = new LoginScreen();
                        loginScreen.start(new Stage());
                        
                    } else {
                        messageLabel.setText("Signup failed! (Username might already exist)");
                        messageLabel.getStyleClass().setAll("message-label", "error"); 
                    }
                } catch (Exception ex) {
                    messageLabel.setText("An error occurred during signup!");
                    messageLabel.getStyleClass().setAll("message-label", "error");
                    System.out.println(ex);
                }
            }
        });
        
        loginLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                LoginScreen loginScreen = new LoginScreen();
                try {
                    primaryStage.close(); 
                    loginScreen.start(new Stage());
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        });


        GridPane grid = new GridPane();
        grid.getStyleClass().add("signup-grid"); 
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(signupButton, 1, 3);
        
        grid.add(messageLabel, 0, 4, 2, 1); 
        GridPane.setHalignment(messageLabel, javafx.geometry.HPos.CENTER);
        
        grid.add(loginLink, 0, 5, 2, 1); 
        GridPane.setHalignment(loginLink, javafx.geometry.HPos.CENTER);
        
        grid.setAlignment(Pos.CENTER);
        root.setCenter(grid);
        root.setTop(topBar);
        
        Scene scene = new Scene(root, 400, 480);
        scene.getStylesheets().add(getClass().getResource("/css/signup_screen_style.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.show();
        signupStage.add(primaryStage);
    }

}