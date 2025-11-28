package dao;

import model.Budgets;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import database.DBConnection;

public class ManageBudgetDAO {
    private static PreparedStatement pstmt = null;
    private static Connection connection = DBConnection.getDBConnection();

    // Insert a new budget
    public static boolean insertBudget(Budgets budget) {

        String CheckOverlapQuery = "SELECT * FROM budgets WHERE user_id = ? AND category_id = ? " +
                "AND start_date <= ? AND end_date >= ?";

        String sql = "INSERT INTO budgets (user_id, category_id, category, budget_name, amount, " +
                "start_date, end_date, current_spent, alert_threshold, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // Check for overlapping budgets

            pstmt = connection.prepareStatement(CheckOverlapQuery);
            // Assuming budgetId is used for checking overlap
            pstmt.setInt(1, budget.getUserId());
            pstmt.setInt(2, budget.getCategoryId());
            pstmt.setDate(3, java.sql.Date.valueOf(budget.getEndDate()));
            pstmt.setDate(4, java.sql.Date.valueOf(budget.getStartDate()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return false; // Overlapping budget found, do not insert
            }

            // adding budget code
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, budget.getUserId());
            pstmt.setInt(2, budget.getCategoryId());
            pstmt.setString(3, budget.getCategory());
            pstmt.setString(4, budget.getBudgetName());
            pstmt.setDouble(5, budget.getAmount());
            pstmt.setDate(6, java.sql.Date.valueOf(budget.getStartDate()));
            pstmt.setDate(7, java.sql.Date.valueOf(budget.getEndDate()));
            pstmt.setDouble(8, budget.getCurrentSpent());
            pstmt.setDouble(9, budget.getAmount() * 0.9);
            pstmt.setBoolean(10, budget.getIsActive());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return false;
    }

    // Update an existing budget
    public static boolean updateBudget(Budgets budget, int userId) {

        // Check for overlapping budgets
        String CheckOverlapQuery = "SELECT * FROM budgets WHERE user_id = ? AND category_id = ? " +
                "AND start_date <= ? AND end_date >= ? AND budget_id != ?";

        String sql = "UPDATE budgets SET   " +
                "budget_name = ?, amount = ?, start_date = ?, end_date = ?, " +
                "current_spent = ?, alert_threshold = ?, is_active = ? " +
                "WHERE budget_id = ? AND user_id = ?";

        try {
            pstmt = connection.prepareStatement(CheckOverlapQuery);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, budget.getCategoryId());
            pstmt.setDate(3, java.sql.Date.valueOf(budget.getEndDate()));
            pstmt.setDate(4, java.sql.Date.valueOf(budget.getStartDate()));
            pstmt.setInt(5, budget.getBudgetId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return false; // Overlapping budget found, do not update
            }

            pstmt = connection.prepareStatement(sql);
            // pstmt.setInt(1, budget.getCategoryId());
            // pstmt.setString(2, budget.getCategory());
            pstmt.setString(1, budget.getBudgetName());
            pstmt.setDouble(2, budget.getAmount());
            pstmt.setDate(3, java.sql.Date.valueOf(budget.getStartDate()));
            pstmt.setDate(4, java.sql.Date.valueOf(budget.getEndDate()));
            pstmt.setDouble(5, budget.getCurrentSpent());
            pstmt.setDouble(6, budget.getAmount() * 0.9);
            pstmt.setBoolean(7, budget.getIsActive());
            pstmt.setInt(8, budget.getBudgetId());
            pstmt.setInt(9, userId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a budget
    public static boolean deleteBudget(int budgetId, int userId) {
        String sql = "DELETE FROM budgets WHERE budget_id = ? AND user_id = ?";

        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, budgetId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    // Get budget by ID
    public static Budgets getBudgetById(int budgetId, int userId) {
        String sql = "SELECT * FROM budgets WHERE budget_id = ? AND user_id = ?";
        Budgets budget = null;

        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, budgetId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                budget = new Budgets(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getString(5),
                        rs.getDouble(6), rs.getDate(7).toLocalDate(), rs.getDate(8).toLocalDate(), rs.getDouble(9),
                        rs.getDouble(10), rs.getBoolean(11), rs.getTimestamp(12));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budget;
    }

    // Get all budgets for a user
    public static List<Budgets> getAllBudgetsForUser(int userId) {
        List<Budgets> budgets = new ArrayList<>();
        String sql = "SELECT * FROM budgets WHERE user_id = ?";

        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Budgets budget = new Budgets(rs.getInt("budget_id"), rs.getInt("user_id"), rs.getInt("category_id"),
                        rs.getString("category"), rs.getString("budget_name"), rs.getDouble("amount"),
                        rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(),
                        rs.getDouble("current_spent"), rs.getDouble("alert_threshold"), rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at"));
                budgets.add(budget);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgets;
    }

    // Get active budgets for a user
    /*
     * public List<Budget> getActiveBudgetsForUser(int userId) throws SQLException {
     * List<Budget> budgets = new ArrayList<>();
     * String sql = "SELECT * FROM budgets WHERE user_id = ? AND is_active = true";
     * 
     * try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
     * pstmt.setInt(1, userId);
     * 
     * try (ResultSet rs = pstmt.executeQuery()) {
     * while (rs.next()) {
     * budgets.add(mapResultSetToBudget(rs));
     * }
     * }
     * }
     * return budgets;
     * }
     */

    // Helper method to map ResultSet to Budget object
    /*
     * private Budget mapResultSetToBudget(ResultSet rs) throws SQLException {
     * Budget budget = new Budget();
     * budget.setBudgetId(rs.getInt("budget_id"));
     * budget.setUserId(rs.getInt("user_id"));
     * budget.setCategoryId(rs.getInt("category_id"));
     * budget.setCategory(rs.getString("category"));
     * budget.setBudgetName(rs.getString("budget_name"));
     * budget.setAmount(rs.getDouble("amount"));
     * budget.setStartDate(rs.getDate("start_date"));
     * budget.setEndDate(rs.getDate("end_date"));
     * budget.setCurrentSpent(rs.getDouble("current_spent"));
     * budget.setAlertThreshold(rs.getDouble("alert_threshold"));
     * budget.setActive(rs.getBoolean("is_active"));
     * budget.setCreatedAt(rs.getTimestamp("created_at"));
     * return budget;
     * }
     */
    public static List<Budgets> matchExpenseWithBudget(int categoryId, int userId) {
        List<Budgets> budgets = new ArrayList<>();
        String sql = "SELECT * FROM budgets WHERE category_id = ?  AND user_id = ?";

        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, categoryId);
            pstmt.setInt(2, userId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Budgets budget = new Budgets(rs.getInt("budget_id"), rs.getInt("user_id"), rs.getInt("category_id"),
                        rs.getString("category"), rs.getString("budget_name"), rs.getDouble("amount"),
                        rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(),
                        rs.getDouble("current_spent"), rs.getDouble("alert_threshold"), rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at"));
                budgets.add(budget);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgets;
    }

    // Update current spent amount for a budget
    public static boolean updateCurrentSpent(int budgetId, double amount, int userId) {
        String sql = "UPDATE budgets SET current_spent = ? WHERE budget_id = ? AND user_id = ?";
        boolean isUpdated = false;

        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, budgetId);
            pstmt.setInt(3, userId);
            isUpdated = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isUpdated;
    }

    public static List<Budgets> currentMatchBudgets(String category, LocalDate date,int userId) {
        String sql = "SELECT * FROM budgets WHERE user_id =? AND category = ? AND start_date <= ? AND end_date >= ? AND is_active = true";
        List<Budgets> budgets = new ArrayList<>();
        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, category);
            pstmt.setDate(3, java.sql.Date.valueOf(date));
            pstmt.setDate(4, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Budgets budget = new Budgets(rs.getInt("budget_id"), rs.getInt("user_id"), rs.getInt("category_id"),
                        rs.getString("category"), rs.getString("budget_name"), rs.getDouble("amount"),
                        rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(),
                        rs.getDouble("current_spent"), rs.getDouble("alert_threshold"), rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at"));
                budgets.add(budget);

            }
           
            
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return budgets;

    }

    public static boolean checkExpensesOfBudget(int budgetId, int userId) {
        String sql = "SELECT COUNT(*) FROM expenses WHERE budget_id = ? AND u_id = ?";
        boolean hasExpenses = false;

        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, budgetId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                hasExpenses = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hasExpenses;
    }
}