package main;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import dao.AdminDAO;
import model.Admin;
import utils.*;
import javafx.stage.Stage;

public class AdminProfileScreen extends Application {
    public static java.util.List<Stage> adminProfileStage = new java.util.ArrayList<>();
    private String currentUsername;
    private String currentEmail;
    private String currentPassword;

    private TextField usernameField;
    private TextField emailField;
    private PasswordField currentPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private Label messageLabel;
    private Button editButton;
    private Button saveButton;
    private Button cancelButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Expense Manager - Admin Profile");

        // Main container
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Admin Profile");
        titleLabel.getStyleClass().add("title-label");

        setData();// initializing data from database
        // Form container
        VBox formContainer = new VBox(20);
        formContainer.getStyleClass().add("form-container");
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(500);
        formContainer.setPadding(new Insets(30));

        // Create form
        GridPane formGrid = createProfileForm();

        HBox buttonBox = createButtonBox();

        formContainer.getChildren().addAll(titleLabel, formGrid, buttonBox);

        // Center everything
        StackPane centerPane = new StackPane(formContainer);
        root.setCenter(centerPane);

        // Create scene and apply CSS
        Scene scene = new Scene(root, 700, 600);
        scene.getStylesheets().add(getClass().getResource("/css/profile_style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(450);
        primaryStage.show();
        adminProfileStage.add(primaryStage);

        // Initial state
        setFieldsEditable(false);
    }

    private GridPane createProfileForm() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));

        // Username
        Label usernameLabel = new Label("Username:");
        usernameLabel.getStyleClass().add("form-label");
        usernameField = new TextField(currentUsername);
        usernameField.getStyleClass().add("text-field");
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        // Email
        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("form-label");
        emailField = new TextField(currentEmail);
        emailField.getStyleClass().add("text-field");
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);

        // Current Password
        Label currentPasswordLabel = new Label("Current Password:");
        currentPasswordLabel.getStyleClass().add("form-label");
        currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Only if changing password");
        currentPasswordField.getStyleClass().add("password-field");
        grid.add(currentPasswordLabel, 0, 2);
        grid.add(currentPasswordField, 1, 2);

        // New Password
        Label newPasswordLabel = new Label("New Password:");
        newPasswordLabel.getStyleClass().add("form-label");
        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Only if changing password");
        newPasswordField.getStyleClass().add("password-field");
        grid.add(newPasswordLabel, 0, 3);
        grid.add(newPasswordField, 1, 3);

        // Confirm Password
        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordLabel.getStyleClass().add("form-label");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Only if changing password");
        confirmPasswordField.getStyleClass().add("password-field");
        grid.add(confirmPasswordLabel, 0, 4);
        grid.add(confirmPasswordField, 1, 4);

        // Message label
        messageLabel = new Label();
        messageLabel.getStyleClass().add("message-label");
        grid.add(messageLabel, 0, 5, 2, 1);

        return grid;
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        editButton = new Button("Edit Profile");
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                enableEditing();
            }
        });

        saveButton = new Button("Save Changes");
        saveButton.getStyleClass().add("save-button");
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleSaveAction();
            }
        });
        saveButton.setDisable(true);

        cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("cancel-button");
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                resetFields();
                setFieldsEditable(false);
                cancelButton.setDisable(true);
                saveButton.setDisable(true);
                editButton.setDisable(false);
            }
        });
        cancelButton.setDisable(true);

        buttonBox.getChildren().addAll(editButton, saveButton, cancelButton);
        return buttonBox;
    }

    private void enableEditing() {
        setFieldsEditable(true);
        editButton.setDisable(true);
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
    }

    private void setFieldsEditable(boolean editable) {
        usernameField.setEditable(editable);
        emailField.setEditable(editable);
        currentPasswordField.setEditable(editable);
        newPasswordField.setEditable(editable);
        confirmPasswordField.setEditable(editable);

        // Visual feedback for edit mode
        if (editable) {
            usernameField.setStyle("-fx-background-color: white;");
            emailField.setStyle("-fx-background-color: white;");
        } else {
            usernameField.setStyle("-fx-background-color: #f9f9f9;");
            emailField.setStyle("-fx-background-color: #f9f9f9;");
        }
    }

    private void resetFields() {

        usernameField.clear();
        emailField.clear();
        usernameField.setText(currentUsername);
        emailField.setText(currentEmail);
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        messageLabel.setText("");
        messageLabel.getStyleClass().removeAll("error-message", "success-message");
        setData();
    }

    private void handleSaveAction() {
        String newUsername = usernameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String currentPasswordInput = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Clear previous message styles
        messageLabel.getStyleClass().removeAll("error-message", "success-message");

        // Validate username
        if (newUsername.isEmpty()) {
            showErrorMessage("Username cannot be empty");
            return;
        }

        // Validate email
        if (newEmail.isEmpty() || !newEmail.contains("@") || !newEmail.contains(".")) {
            showErrorMessage("Please enter a valid email address");
            return;
        }

        // Check if password is being changed
        boolean changingPassword = !newPassword.isEmpty() || !confirmPassword.isEmpty()
                || !currentPasswordInput.isEmpty();

        if (changingPassword) {
            // Validate current password if changing password
            if (currentPasswordInput.isEmpty()) {
                showErrorMessage("Please enter your current password to change it");
                return;
            }

            if (!AdminDAO.sha256(currentPasswordInput).equals(currentPassword)) {
                showErrorMessage("Current password is incorrect");
                return;
            }

            // Validate new password if provided
            if (!newPassword.isEmpty()) {
                if (newPassword.length() > 12) {
                    showErrorMessage("Password must be 12 characters or less");
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    showErrorMessage("New passwords don't match");
                    return;
                }
            } else {
                showErrorMessage("Please enter a new password");
                return;
            }
        }

        /*
         * currentUsername = newUsername;
         * currentEmail = newEmail;
         * if (changingPassword && !newPassword.isEmpty()) {
         * currentPassword = newPassword;
         * }
         */
        updateAdminData(Session.getCurrentUser(), newUsername, newEmail, newPassword);
        setData();
        // Show success
        // messageLabel.getStyleClass().add("success-message");
        // messageLabel.setText("Profile updated successfully!");

        // Reset UI
        setFieldsEditable(false);
        editButton.setDisable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    private void showErrorMessage(String message) {
        messageLabel.getStyleClass().add("error-message");
        messageLabel.setText("Error: " + message);
    }

    private void setData() {
        Admin currentUser = AdminDAO.getAdminById(Session.getCurrentUser());
        if (currentUser != null) {
            currentUsername = currentUser.getAdminName();
            currentEmail = currentUser.getAdminEmail();
            currentPassword = currentUser.getPassword();
            // usernameField.setText(currentUsername);
            // emailField.setText(currentEmail);
        } else {
            showErrorMessage("Failed to load admin data");
        }
    }

    private void updateAdminData(int id, String username, String email, String password) {
        String hashPassword;
        if (password == null || password.isEmpty()) {
            hashPassword = currentPassword; // Use current password if not changing
        }else{
            hashPassword = AdminDAO.sha256(password);

        }

        Admin updatedAdminDate = new Admin(id, username, email, hashPassword);
        boolean success = AdminDAO.updateAdmin(updatedAdminDate);
        if (success) {
            messageLabel.getStyleClass().add("success-message");
            messageLabel.setText("Profile updated successfully!");
            EmailUtil.sendMail(email, " budgetbuddy6353@gmail.com", "Profile Update Confirmation - Budget Buddy", "Hello " + username + ",\n\nYour profile has been successfully updated in Budget Buddy.");
        } else {
            showErrorMessage("Failed to update profile");
        }

    }

}