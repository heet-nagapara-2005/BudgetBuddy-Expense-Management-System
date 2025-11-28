import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.event.EventHandler;
//import java.beans.EventHandler;
import java.time.LocalDate;
import java.time.YearMonth;
//import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class TestExpenseAnalysisCode extends Application {
     
  public static  class Expense {
        private String category;
        private double amount;
        private LocalDate date;
    
        public Expense(String category, double amount, LocalDate date) {
            this.category = category;
            this.amount = amount;
            this.date = date;
        }
    
        // Getters
        public String getCategory() { return category; }
        public double getAmount() { return amount; }
        public LocalDate getDate() { return date; }
    }
    
    // Sample expense data
    private List<Expense> expenses = Arrays.asList(
        new Expense("Food", 500.0, LocalDate.of(2023, 6, 1)),
        new Expense("Transport", 300.0, LocalDate.of(2023, 6, 1)),
        new Expense("Food", 200.0, LocalDate.of(2023, 6, 2)),
        new Expense("Shopping", 1000.0, LocalDate.of(2023, 6, 3)),
        new Expense("Transport", 150.0, LocalDate.of(2023, 6, 3)),
        new Expense("Food", 350.0, LocalDate.of(2023, 6, 15)),
        new Expense("Entertainment", 800.0, LocalDate.of(2023, 6, 20))
    );

    @Override
    public void start(Stage primaryStage) {
        // Main container
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        // 1. Category-wise Chart Section
        Label categoryLabel = new Label("Category-wise Expenses");
        categoryLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Date pickers for category chart
        HBox dateRangeBox = new HBox(10);
        DatePicker startDatePicker = new DatePicker(LocalDate.now().withDayOfMonth(1));
        DatePicker endDatePicker = new DatePicker(LocalDate.now());
        Button updateCategoryChartBtn = new Button("Update Chart");
        dateRangeBox.getChildren().addAll(
            new Label("From:"), startDatePicker,
            new Label("To:"), endDatePicker,
            updateCategoryChartBtn
        );

        // Category chart
        BarChart<String, Number> categoryChart = createCategoryChart();

        // 2. Day-wise Chart Section
        Label dayLabel = new Label("Daily Expenses");
        dayLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Month/Year selection for day chart
        HBox monthSelectionBox = new HBox(10);
        ComboBox<String> monthCombo = new ComboBox<>(
            FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )
        );
        monthCombo.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
        Spinner<Integer> yearSpinner = new Spinner<>(2020, 2030, LocalDate.now().getYear());
        Button updateDayChartBtn = new Button("Update Chart");
        monthSelectionBox.getChildren().addAll(
            new Label("Month:"), monthCombo,
            new Label("Year:"), yearSpinner,
            updateDayChartBtn
        );

        // Day chart
        BarChart<String, Number> dayChart = createDayChart();

        // Add all components to root
        root.getChildren().addAll(
            categoryLabel, dateRangeBox, categoryChart,
            dayLabel, monthSelectionBox, dayChart
        );

        // Initial chart updates
        updateCategoryChart(categoryChart, startDatePicker.getValue(), endDatePicker.getValue());
        updateDayChart(dayChart, monthCombo.getSelectionModel().getSelectedIndex() + 1, 
                      yearSpinner.getValue());

        // Button actions
        updateCategoryChartBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateCategoryChart(
                    categoryChart, 
                    startDatePicker.getValue(), 
                    endDatePicker.getValue()
                );
            }
        });

        updateDayChartBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateDayChart(
                    dayChart,
                    monthCombo.getSelectionModel().getSelectedIndex() + 1,
                    yearSpinner.getValue()
                );
            }
        });

        // Show stage
        Scene scene = new Scene(root, 900, 800);
        primaryStage.setTitle("Expense Analysis Charts");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private BarChart<String, Number> createCategoryChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Expenses by Category");
        chart.setLegendVisible(false);
        chart.setPrefHeight(300);
        return chart;
    }

    private BarChart<String, Number> createDayChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Daily Expenses");
        chart.setLegendVisible(false);
        chart.setPrefHeight(300);
        return chart;
    }

    private void updateCategoryChart(BarChart<String, Number> chart, LocalDate start, LocalDate end) {
        // Filter expenses by date range
        Map<String, Double> categoryTotals = expenses.stream()
            .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
            .collect(Collectors.groupingBy(
                Expense::getCategory,
                Collectors.summingDouble(Expense::getAmount)
            ));

        // Update chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        categoryTotals.forEach((category, total) -> 
            series.getData().add(new XYChart.Data<>(category, total))
        );

        chart.getData().clear();
        chart.getData().add(series);
    }

    private void updateDayChart(BarChart<String, Number> chart, int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        // Group expenses by day
        Map<Integer, Double> dailyTotals = expenses.stream()
            .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
            .collect(Collectors.groupingBy(
                e -> e.getDate().getDayOfMonth(),
                Collectors.summingDouble(Expense::getAmount)
            ));

        // Fill all days of month (even with 0 expenses)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            double amount = dailyTotals.getOrDefault(day, 0.0);
            series.getData().add(new XYChart.Data<>(String.valueOf(day), amount));
        }

        chart.getData().clear();
        chart.getData().add(series);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

