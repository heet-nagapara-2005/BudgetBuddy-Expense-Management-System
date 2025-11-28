package main;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class Main extends Application {

    public static Stage mainStage;
    private Stage splashStage;

    @Override
    public void start(Stage stage) {

        stage.hide();
        mainStage = stage;

        openLogoScreen();

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(4.0),
                        new javafx.event.EventHandler<javafx.event.ActionEvent>() {
                            @Override
                            public void handle(javafx.event.ActionEvent event) {
                                if (splashStage != null) {
                                    splashStage.close();
                                }

                                showMainAppScreen(mainStage);
                            }
                        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void showMainAppScreen(Stage stage) {

        LoginScreen loginScreen = new LoginScreen();
        try {
            stage.close();

            loginScreen.start(new Stage());

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    private void openLogoScreen() {
        splashStage = new Stage();
        BudgetBuddyLogoScreen logoScreen = new BudgetBuddyLogoScreen();
        try {
            logoScreen.start(splashStage);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}