package model;

import java.sql.Timestamp;

public class GenerateReport {
    private int reportId;
    private int userId;
    private String username;
    private String email;
    private String reportType;
    private String reportPeriod;
    private String allCategoriesAmount;
    private String highestAmountCategory;
    private String lowestAmountCategory;
    private Double averageSpending;
    private int totalTransactions;
    private Timestamp createdAt;

    // Default constructor
    public GenerateReport() {}

    // Parameterized constructor
    public GenerateReport(int reportId, int userId, String username, String email, 
                          String reportType, String reportPeriod, String allCategoriesAmount,
                          String highestAmountCategory, String lowestAmountCategory,
                          Double averageSpending, int totalTransactions, Timestamp createdAt) {
        this.reportId = reportId;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.reportType = reportType;
        this.reportPeriod = reportPeriod;
        this.allCategoriesAmount = allCategoriesAmount;
        this.highestAmountCategory = highestAmountCategory;
        this.lowestAmountCategory = lowestAmountCategory;
        this.averageSpending = averageSpending;
        this.totalTransactions = totalTransactions;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportPeriod() {
        return reportPeriod;
    }

    public void setReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
    }

    public String getAllCategoriesAmount() {
        return allCategoriesAmount;
    }

    public void setAllCategoriesAmount(String allCategoriesAmount) {
        this.allCategoriesAmount = allCategoriesAmount;
    }

    public String getHighestAmountCategory() {
        return highestAmountCategory;
    }

    public void setHighestAmountCategory(String highestAmountCategory) {
        this.highestAmountCategory = highestAmountCategory;
    }

    public String getLowestAmountCategory() {
        return lowestAmountCategory;
    }

    public void setLowestAmountCategory(String lowestAmountCategory) {
        this.lowestAmountCategory = lowestAmountCategory;
    }

    public Double getAverageSpending() {
        return averageSpending;
    }

    public void setAverageSpending(Double averageSpending) {
        this.averageSpending = averageSpending;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
