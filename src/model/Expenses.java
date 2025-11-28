package model;

import java.sql.Timestamp;

public class Expenses {
    private int e_id;
    private int u_id;
    private int c_id;
    private String category;
    private double amount;
    private String description;
    private Timestamp e_date;
    private Timestamp createdDate;
    private int budgetId; 
    private String budgetName;

    // Constructor
    public Expenses(int e_id, int u_id, int c_id, String category, double amount, String description, Timestamp e_date,
            Timestamp createdDate, int budgetId, String budgetName) {
        this.e_id = e_id;
        this.u_id = u_id;
        this.c_id = c_id;
        this.category = category;
        this.amount = amount;

        this.description = description;
        this.e_date = e_date;
        this.createdDate = createdDate;

        this.budgetId = budgetId;
        this.budgetName = budgetName;
    }

    // Getters & Setters
    public int getE_id() {
        return e_id;
    }

    public void setE_id(int e_id) {
        this.e_id = e_id;
    }

    public int getU_id() {
        return u_id;
    }

    public void setU_id(int u_id) {
        this.u_id = u_id;
    }

    public int getC_id() {
        return c_id;
    }

    public void setC_id(int c_id) {
        this.c_id = c_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String c) {
        this.category = c;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getE_date() {
        return e_date;
    }

    public void setE_date(Timestamp e_date) {
        this.e_date = e_date;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
    public int getBudgetId() {
        return budgetId;
    }
    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }
    public String getBudgetName() {
        return budgetName;
    }
    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }
}
