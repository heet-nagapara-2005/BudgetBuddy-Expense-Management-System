import javafx.application.Application;
//import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.chart.*;
//import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.*;

public class BarChartExample extends Application {

    @Override
    public void start(Stage stage) {
        // 1. Sample Data (Replace with your actual data)
        List<Expense> expenses = Arrays.asList(
            new Expense("01-06-2024", "Food", 1500),
            new Expense("01-06-2024", "Transport", 800),
            new Expense("02-06-2024", "Food", 2000),
            new Expense("03-06-2024", "Rent", 10000),
            new Expense("03-06-2024", "Entertainment", 1200)
        );

        // 2. Create Charts
        PieChart pieChart = createPieChart(expenses);
        BarChart<String, Number> barChart = createBarChart(expenses);

        // 3. Layout Setup
        HBox root = new HBox(20, pieChart, barChart);
        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Expense Analysis: Pie Chart (Category) + Bar Chart (Amount)");
        stage.show();
    }

    // Pie Chart (Category-wise Distribution)
    private PieChart createPieChart(List<Expense> expenses) {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Category-wise Expense Distribution");

        // Calculate total amount per category
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : expenses) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }

        // Add data to pie chart
        categoryTotals.forEach((category, amount) -> 
            pieChart.getData().add(new PieChart.Data(category, amount))
        );

        return pieChart;
    }

    // Bar Chart (Amount-wise Comparison)
    private BarChart<String, Number> createBarChart(List<Expense> expenses) {
        CategoryAxis xAxis = new CategoryAxis(); // Categories
        NumberAxis yAxis = new NumberAxis();     // Amount
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Amount per Category");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Expenses");

        // Group by Category
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : expenses) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }

        // Add to bar chart
        categoryTotals.forEach((category, amount) -> 
            series.getData().add(new XYChart.Data<>(category, amount))
        );

        barChart.getData().add(series);
        return barChart;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class Expense {
   //private String date;
    private String category;
    private double amount;

    public Expense(String date, String category, double amount) {
  //      this.date = date;
        this.category = category;
        this.amount = amount;
    }

    // Getters
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
}