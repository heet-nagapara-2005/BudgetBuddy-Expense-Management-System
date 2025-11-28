import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Demo extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // MySQL Database se connect karne ka code
            String url = "jdbc:mysql://localhost:3306/crud"; // Database URL
            String username = "root"; // Database username
            String password = ""; // Database password
            
            Connection conn = DriverManager.getConnection(url, username, password);
            
            if (conn != null) {
                System.out.println("Database connected successfully!");
            } else {
                System.out.println("Failed to connect to database.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
