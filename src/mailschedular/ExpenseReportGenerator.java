package mailschedular;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.sql.Timestamp;

import model.*;

public class ExpenseReportGenerator {

    public static List<String> reportGenerating(List<GenerateReport> reports) {
        
        List<String> generatedReports = new java.util.ArrayList<>();
        if (reports == null || reports.isEmpty()) {
            System.out.println("No reports to generate.");
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

        for (GenerateReport r : reports) {
            StringBuilder reportContent = new StringBuilder();
            String userName = r.getUsername();
            // String userEmail = r.getEmail();
            String reportType = r.getReportType();
            String totalExpenseType = "";
            if (reportType.equals("monthly")) {
                totalExpenseType = "Month";
            } else {
                totalExpenseType = "Year";
            }
          

            String reportPeriod = r.getReportPeriod();
            String temp = r.getAllCategoriesAmount();
            String allCategoryAmount;
            double total = 0;
            if (temp == null || temp.equals("no categories")) {
                allCategoryAmount = "No Categories";
            } else {
                allCategoryAmount = temp.replace("\"", "")
                        .replace("{", "")
                        .replace("}", "");
                if (allCategoryAmount.contains(",")) {
                    for (String categoryAmount : allCategoryAmount.split(",")) {
                        String[] parts = categoryAmount.split(":");
                        // String categoryName = parts[0].trim();
                        double amount = Double.parseDouble(parts[1].trim());
                        total += amount;
                    }
                } else {
                    String[] parts = allCategoryAmount.split(":");
                    // String categoryName = parts[0].trim();
                    double amount = Double.parseDouble(parts[1].trim());
                    total += amount;
                }
            }
          

            String highestAmountCategory = r.getHighestAmountCategory();
            String lowestAmountCategory = r.getLowestAmountCategory();
            Double averageSpending = r.getAverageSpending();
            int totalTransactions = r.getTotalTransactions();
            Timestamp createdAt = r.getCreatedAt();
            LocalDate generatedOn = createdAt.toLocalDateTime().toLocalDate();

            reportContent.append("Dear ").append(userName).append(",\n\n")
                    .append("Please find below your "+reportType+" expense summary from Budget Buddy:\n\n")
                    .append("Report Type: ").append(reportType).append("\n")
                    .append("Report Period: ").append(reportPeriod).append("\n")

                    .append("Total Expenses of this " + totalExpenseType + ": ₹").append(total).append("\n")
                    .append("Number of Transactions: ").append(totalTransactions).append("\n")
                    .append("All category with amount(₹): ").append(allCategoryAmount).append("\n")
                    .append("Highest Expense Category(₹): ").append(highestAmountCategory).append("\n")
                    .append("Lowest Expense Category(₹): ").append(lowestAmountCategory).append("\n")
                    .append("Average Daily Spending in this " + totalExpenseType + "(₹):").append(averageSpending)
                    .append("\n")
                    .append("\nGenerated on: ").append(generatedOn.format(formatter)).append("\n\n")
                    .append("Thank you for using Budget Buddy to manage your finances.\n")
                    .append("--------------------------------------------------\n");
            generatedReports.add(reportContent.toString());

        }
        System.out.println("Report generation completed. Total reports generated: " + generatedReports.size());

        return generatedReports;
    }
}
