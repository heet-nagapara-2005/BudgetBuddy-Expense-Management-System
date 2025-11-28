
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Stack;

public class SingleScreenModel extends Application {

    // Stack to keep track of open screens
    private Stack<Stage> screenStack = new Stack<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login Screen");

        Button loginButton = new Button("Login (Open Main Screen)");
        loginButton.setOnAction(e -> {
            Stage mainScreen = createMainScreen();
            openScreen(mainScreen);
        });

        VBox loginLayout = new VBox(20, loginButton);
        Scene loginScene = new Scene(loginLayout, 300, 200);

        primaryStage.setScene(loginScene);
        primaryStage.show();

        // Push login screen to stack
        screenStack.push(primaryStage);
    }

    // Method to open a new screen
    private void openScreen(Stage newScreen) {
        if (!screenStack.isEmpty()) {
            screenStack.peek().hide();  // hide previous screen
        }
        newScreen.show();
        screenStack.push(newScreen);
    }

    // Method to close current screen and go back
    private void closeCurrentScreen() {
        if (!screenStack.isEmpty()) {
            Stage current = screenStack.pop();
            current.close();
        }
        if (!screenStack.isEmpty()) {
            screenStack.peek().show(); // show previous screen
        }
    }

    // Example: Main Screen
    private Stage createMainScreen() {
        Stage stage = new Stage();
        stage.setTitle("Main Screen");

        Button dashboardButton = new Button("Open Dashboard");
        dashboardButton.setOnAction(e -> {
            Stage dashboardScreen = createDashboardScreen();
            openScreen(dashboardScreen);
        });

        Button logoutButton = new Button("Logout (Back to Login)");
        logoutButton.setOnAction(e -> closeCurrentScreen());

        VBox layout = new VBox(20, dashboardButton, logoutButton);
        Scene scene = new Scene(layout, 400, 250);
        stage.setScene(scene);

        return stage;
    }

    // Example: Dashboard Screen
    private Stage createDashboardScreen() {
        Stage stage = new Stage();
        stage.setTitle("Dashboard Screen");

        Button backButton = new Button("Back to Main");
        backButton.setOnAction(e -> closeCurrentScreen());

        VBox layout = new VBox(20, backButton);
        Scene scene = new Scene(layout, 400, 250);
        stage.setScene(scene);

        return stage;
    }
}
