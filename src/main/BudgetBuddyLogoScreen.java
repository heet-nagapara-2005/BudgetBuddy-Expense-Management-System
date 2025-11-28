package main;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BudgetBuddyLogoScreen extends Application {
    
    
    private static final double SPLASH_DURATION_SECONDS = 4.0; 

    @Override
    public void start(Stage stage) {
        try {
            WebView webView = new WebView();
            WebEngine engine = webView.getEngine();

             String htmlPath = getClass().getResource("/css/BudgetBuddy_logo.html").toExternalForm();
            engine.load(htmlPath);

            StackPane root = new StackPane(webView);
            Scene scene = new Scene(root, 800, 600);

            stage.setTitle("BudgetBuddy Logo Splash Screen");
            stage.setScene(scene);
            stage.show();

           
            Timeline timeline = new Timeline(
                new KeyFrame(
                    Duration.seconds(SPLASH_DURATION_SECONDS), 
                    new javafx.event.EventHandler<javafx.event.ActionEvent>() {
                        @Override
                        public void handle(javafx.event.ActionEvent event) {
                            stage.close(); 
                        }
                    }
                )
            );
            timeline.setCycleCount(1); 
            timeline.play(); 


        } catch (Exception e) {
            System.err.println("Error loading HTML file. Check the path: /css/BudgetBuddy_logo.html");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}