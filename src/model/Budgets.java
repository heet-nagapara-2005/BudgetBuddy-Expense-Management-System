package model;



//import java.math.BigDecimal;
import java.time.LocalDate;

import java.sql.Timestamp; // Added import for Timestamp

public class Budgets{
    private int budgetId;
    private int userId;
    private int categoryId;
    private String category;
    private String budgetName;
    private double amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private double currentSpent;
    private double alertThreshold;
    private boolean isActive;
    private Timestamp createdAt; // Changed from LocalDateTime to Timestamp

    // Constructors
  /* public Budget() {
       // this.currentSpent = BigDecimal.ZERO;
       // this.isActive = true;
        //this.createdAt = LocalDateTime.now();
    } */  

    public Budgets(int budgetId,int userId, int categoryId, String category, String budgetName, 
                 double amount, LocalDate startDate, LocalDate endDate,double currentSpent,
                 double alertThreshold, boolean isActive, Timestamp createdAt) { // Changed from LocalDateTime to Timestamp
        //this();
        this.budgetId = budgetId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.category = category;
        this.budgetName = budgetName;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentSpent = currentSpent;
        this.alertThreshold = alertThreshold;
        this.isActive = isActive;
        this.createdAt = createdAt; // Changed from LocalDateTime to Timestamp
    }

    // Getters and Setters
    public int getBudgetId() {
        return budgetId;
    }

     public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getCurrentSpent() {
        return currentSpent;
    }

    public void setCurrentSpent(double currentSpent) {
        this.currentSpent = currentSpent;
    }

    public double getAlertThreshold() {
        return alertThreshold;
    }

    public void setAlertThreshold(double alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public boolean getIsActive() {
        return isActive;
    }
    public String getStatus() {
        return isActive ? "Active" : "Inactive";
    }

    public void setIsActive(boolean active) {
        isActive = active;
 
    }
    public Timestamp getCreatedAt() { // Changed from LocalDateTime to Timestamp
        return createdAt;
    }
}