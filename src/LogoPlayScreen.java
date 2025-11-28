import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class LogoPlayScreen extends Application {
    @Override
    public void start(Stage stage) {
        try {
            WebView webView = new WebView();
            WebEngine engine = webView.getEngine();

            
            String htmlPath = getClass().getResource("/css/BudgetBuddy_logo.html").toExternalForm();
            engine.load(htmlPath);

            StackPane root = new StackPane(webView);
            Scene scene = new Scene(root, 800, 600);

            stage.setTitle("BudgetBuddy Logo in JavaFX");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
