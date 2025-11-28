package dao;

import model.Expenses;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.sql.Connection;
import database.DBConnection;

import model.Budgets;
import utils.EmailUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import utils.Session;

public class ExpenseDAO {
    private static int totalExpenses;
    private static double totalAmountSpent;

    public static int getTotalExpenses() {
        return totalExpenses;
    }

    public static double getTotalAmountSpent() {
        return totalAmountSpent;
    }

    public static boolean addExpense(Expenses e) {

        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "INSERT INTO expenses(e_id,u_id,c_id, category, description,ammount,expense_date,budget_id,budget_name) VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, e.getE_id());
            ps.setInt(2, e.getU_id());
            ps.setInt(3, e.getC_id());
            ps.setString(4, e.getCategory());
            ps.setString(5, e.getDescription());
            ps.setDouble(6, e.getAmount());
            ps.setTimestamp(7, e.getE_date());
            if (e.getBudgetId() != 0) {
                ps.setInt(8, e.getBudgetId());
            } else {
                ps.setString(8, null); // Set budget_id to NULL if not provided
            }

            ps.setString(9, e.getBudgetName());
            System.out.println(e.getBudgetName());

            int i = ps.executeUpdate();

            if (i > 0) {
                if (e.getBudgetId() == 0) {
                    return true; // If no budget is associated, we can return true here
                }
                Budgets b = ManageBudgetDAO.getBudgetById(e.getBudgetId(), e.getU_id());
                Double totalCurrentSpent = b.getCurrentSpent() + e.getAmount();
                boolean updated = ManageBudgetDAO.updateCurrentSpent(b.getBudgetId(), totalCurrentSpent,
                        Session.getCurrentUser());

                if (updated) {

                    b.setCurrentSpent(totalCurrentSpent);
                }
                if (b.getAlertThreshold() <= b.getCurrentSpent()) {
                    String to = UserDAO.getUserById(Session.getCurrentUser()).getEmail();
                    EmailUtil.sendMail(
                            to,
                            " budgetbuddy6353@gmail.com", "Budget alert",
                            "You have spent " + b.getCurrentSpent() + " in category " + b.getCategory()
                                    +
                                    ". Your budget limit is " + b.getAmount()
                                    + ". Please check your budget.");
                }
            }

            /*
             * LocalDate expenseDate = e.getE_date().toLocalDateTime().toLocalDate();
             * // List<Budgets> budget =
             * // ManageBudgetDAO.getAllBudgetsForUser(Session.getCurrentUser());
             * List<Budgets> budget = ManageBudgetDAO.matchExpenseWithBudget(e.getC_id(),
             * e.getU_id());
             * // String category = e.getCategory();
             * for (Budgets b : budget) {
             * 
             * // if (b.getCategory().equals(category)) {
             * 
             * // if (b.getCategory().trim().equals(category.trim())) {
             * if (!expenseDate.isBefore(b.getStartDate()) &&
             * !expenseDate.isAfter(b.getEndDate())) {
             * 
             * if (b.getIsActive() == true) {
             * 
             * Double totalCurrentSpent = b.getCurrentSpent() + e.getAmount();
             * boolean updated = ManageBudgetDAO.updateCurrentSpent(b.getBudgetId(),
             * totalCurrentSpent,
             * Session.getCurrentUser());
             * 
             * if (updated) {
             * 
             * b.setCurrentSpent(totalCurrentSpent);
             * }
             * if (b.getAlertThreshold() <= b.getCurrentSpent()) {
             * String to = UserDAO.getUserById(Session.getCurrentUser()).getEmail();
             * EmailUtil.sendMail(
             * to,
             * " budgetbuddy6353@gmail.com", "Budget alert",
             * "You have spent " + b.getCurrentSpent() + " in category " + b.getCategory()
             * +
             * ". Your budget limit is " + b.getAmount()
             * + ". Please check your budget.");
             * }
             * 
             * }
             * 
             * }
             * 
             * // }
             * }
             * 
             * }
             */
            return i > 0;
        } catch (SQLException se) {
            System.out.println(se);

        }
        return false;

    }

    public static List<Expenses> getAllExpenses() {
        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "SELECT * FROM expenses where u_id =? ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Session.getCurrentUser());
            ResultSet rs = ps.executeQuery();
            List<Expenses> expensesList = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("e_id");
                int userId = rs.getInt("u_id");
                int categoryId = rs.getInt("c_id");
                String category = rs.getString("category");
                String description = rs.getString("description");
                double amount = rs.getDouble("ammount");
                Timestamp date = rs.getTimestamp("expense_date");

                Timestamp c_date = rs.getTimestamp("created_date");
                int budgetId = rs.getInt("budget_id");
                String budgetName = rs.getString("budget_name");

                Expenses e = new Expenses(id, userId, categoryId, category, amount, description, date, c_date, budgetId,
                        budgetName);
                expensesList.add(e);
            }
            return expensesList;
        } catch (SQLException se) {
            System.out.println(se);
        }
        return null;
    }

    public static boolean deleteExpense(int id, int userId) {
        try {
            Expenses e = ExpenseDAO.getExpenseById(id, userId);

            Connection conn = DBConnection.getDBConnection();
            String sql = "DELETE FROM expenses WHERE e_id=?  and u_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);
            ps.setInt(2, userId); // replace with current logged-in user id
            int i = ps.executeUpdate();

            if (i > 0) {
                if (e.getBudgetId() == 0) {
                    return true; // If no budget is associated, we can return true here
                }
                Budgets b = ManageBudgetDAO.getBudgetById(e.getBudgetId(), e.getU_id());
                Double totalCurrentSpent = b.getCurrentSpent() - e.getAmount();
                ManageBudgetDAO.updateCurrentSpent(b.getBudgetId(), totalCurrentSpent,
                        e.getU_id());

                /*
                 * LocalDate expenseDate = e.getE_date().toLocalDateTime().toLocalDate();
                 * List<Budgets> budget = ManageBudgetDAO.matchExpenseWithBudget(e.getC_id(),
                 * e.getU_id());
                 * 
                 * for (Budgets b : budget) {
                 * 
                 * if (!expenseDate.isBefore(b.getStartDate()) &&
                 * !expenseDate.isAfter(b.getEndDate())) {
                 * 
                 * if (b.getIsActive() == true) {
                 * 
                 * Double totalCurrentSpent = b.getCurrentSpent() - e.getAmount();
                 * ManageBudgetDAO.updateCurrentSpent(b.getBudgetId(), totalCurrentSpent,
                 * Session.getCurrentUser());
                 * 
                 * }
                 * 
                 * }
                 * 
                 * }
                 */

            }
            return i > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public static boolean updateExpense(Expenses e, int userId) {
        try {
            int expenseId = e.getE_id();
            Expenses oldExpenses = ExpenseDAO.getExpenseById(expenseId, userId);

            Connection conn = DBConnection.getDBConnection();
            String sql = "update expenses SET c_id=?, category=?, description=?, ammount=?, expense_date=?,budget_id=?,budget_name=? WHERE e_id=? and u_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, e.getC_id());
            ps.setString(2, e.getCategory());
            ps.setString(3, e.getDescription());
            ps.setDouble(4, e.getAmount());
            ps.setTimestamp(5, e.getE_date());
            ps.setInt(6, e.getBudgetId());
            ps.setString(7, e.getBudgetName());
            ps.setInt(8, e.getE_id());
            ps.setInt(9, userId);

            // replace with current logged-in user id
            int i = ps.executeUpdate();

            if (i > 0) {
                Budgets oldBudget = ManageBudgetDAO.getBudgetById(oldExpenses.getBudgetId(), userId);
                if (oldExpenses.getBudgetId() != 0 && oldBudget != null) {
                    double totalCurrentSpent = oldBudget.getCurrentSpent() - oldExpenses.getAmount();
                    ManageBudgetDAO.updateCurrentSpent(oldBudget.getBudgetId(), totalCurrentSpent,
                            userId);
                }

                if (e.getBudgetId() != 0) {
                    Budgets newBudget = ManageBudgetDAO.getBudgetById(e.getBudgetId(), userId);
                    double totalCurrentSpent = newBudget.getCurrentSpent() + e.getAmount();
                    ManageBudgetDAO.updateCurrentSpent(newBudget.getBudgetId(), totalCurrentSpent,
                            userId);
                }

                /*
                 * if (oldExpenses.getC_id() != e.getC_id()) {
                 * // If category has changed, we need to update the budget accordingly
                 * List<Budgets> oldBudget =
                 * ManageBudgetDAO.matchExpenseWithBudget(oldExpenses.getC_id(), userId);
                 * for (Budgets b : oldBudget) {
                 * LocalDate expenseDate =
                 * oldExpenses.getE_date().toLocalDateTime().toLocalDate();
                 * if (!expenseDate.isBefore(b.getStartDate()) &&
                 * !expenseDate.isAfter(b.getEndDate())) {
                 * if (b.getIsActive() == true) {
                 * Double totalCurrentSpent = b.getCurrentSpent() - oldExpenses.getAmount();
                 * ManageBudgetDAO.updateCurrentSpent(b.getBudgetId(), totalCurrentSpent,
                 * Session.getCurrentUser());
                 * }
                 * }
                 * }
                 * }
                 * 
                 * LocalDate expenseDate = e.getE_date().toLocalDateTime().toLocalDate();
                 * List<Budgets> budget = ManageBudgetDAO.matchExpenseWithBudget(e.getC_id(),
                 * e.getU_id());
                 * 
                 * for (Budgets b : budget) {
                 * 
                 * if (!expenseDate.isBefore(b.getStartDate()) &&
                 * !expenseDate.isAfter(b.getEndDate())) {
                 * 
                 * if (b.getIsActive() == true) {
                 * if (oldExpenses.getC_id() != e.getC_id()) {
                 * // If category has changed, we need to update the budget accordingly
                 * Double totalCurrentSpent = b.getCurrentSpent() + e.getAmount();
                 * ManageBudgetDAO.updateCurrentSpent(b.getBudgetId(), totalCurrentSpent,
                 * Session.getCurrentUser());
                 * } else {
                 * Double totalCurrentSpent = (b.getCurrentSpent() - oldExpenses.getAmount())
                 * + e.getAmount();
                 * ManageBudgetDAO.updateCurrentSpent(b.getBudgetId(), totalCurrentSpent,
                 * Session.getCurrentUser());
                 * }
                 * 
                 * }
                 * 
                 * }
                 * 
                 * }
                 */

            }

            return i > 0;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return false;
    }

    public static int totalExpenses(int userId) {
        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "SELECT Count(*) FROM expenses WHERE u_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException se) {
            System.out.println(se);
        }
        return 0;
    }

    public static double totalAmountSpent(int userId) {
        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "SELECT SUM(ammount) FROM expenses WHERE u_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException se) {
            System.out.println(se);
        }
        return 0.0;
    }

    public static Expenses getExpenseById(int expenseId, int userId) {
        String sql = "SELECT * FROM expenses WHERE e_id = ? AND u_id = ?";
        Expenses expense = null;

        try {
            Connection conn = DBConnection.getDBConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, expenseId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                expense = new Expenses(rs.getInt("e_id"), rs.getInt("u_id"), rs.getInt("c_id"),
                        rs.getString("category"), rs.getDouble("ammount"), rs.getString("description"),
                        rs.getTimestamp("expense_date"), rs.getTimestamp("created_date"), rs.getInt("budget_id"),
                        rs.getString("budget_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expense;
    }

    public static boolean hasExpenseOutSideNewDateRange(LocalDate start, LocalDate end, int budgetId, int userId) {
        String sql = "SELECT * FROM expenses WHERE budget_id = ? AND u_id = ? AND expense_date <= ? AND expense_date >= ?";
        
        try {
            Connection conn = DBConnection.getDBConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, budgetId);
            pstmt.setInt(2, userId);
            pstmt.setDate(3, java.sql.Date.valueOf(start));
            pstmt.setDate(4, java.sql.Date.valueOf(end));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {

                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    public static List<Expenses> getExpensesByUserId(int userId){

        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "SELECT * FROM expenses where u_id =? ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Expenses> expensesList = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("e_id");
                int userId1 = rs.getInt("u_id");
                int categoryId = rs.getInt("c_id");
                String category = rs.getString("category");
                String description = rs.getString("description");
                double amount = rs.getDouble("ammount");
                Timestamp date = rs.getTimestamp("expense_date");

                Timestamp c_date = rs.getTimestamp("created_date");
                int budgetId = rs.getInt("budget_id");
                String budgetName = rs.getString("budget_name");

                Expenses e = new Expenses(id, userId1, categoryId, category, amount, description, date, c_date, budgetId,
                        budgetName);
                expensesList.add(e);
            }
            return expensesList;
        } catch (SQLException se) {
            System.out.println(se);
        }
        return null;
    }

    public static List<Expenses> getAllExpensesById(int userId) {
        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "SELECT * FROM expenses where u_id =? ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Expenses> expensesList = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("e_id");
                //int userId = rs.getInt("u_id");
                int categoryId = rs.getInt("c_id");
                String category = rs.getString("category");
                String description = rs.getString("description");
                double amount = rs.getDouble("ammount");
                Timestamp date = rs.getTimestamp("expense_date");

                Timestamp c_date = rs.getTimestamp("created_date");
                int budgetId = rs.getInt("budget_id");
                String budgetName = rs.getString("budget_name");

                Expenses e = new Expenses(id, userId, categoryId, category, amount, description, date, c_date, budgetId,
                        budgetName);
                expensesList.add(e);
            }
            return expensesList;
        } catch (SQLException se) {
            System.out.println(se);
        }
        return null;
    }

}