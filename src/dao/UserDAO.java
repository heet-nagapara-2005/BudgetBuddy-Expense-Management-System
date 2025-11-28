package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;
import database.DBConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {

    public static boolean addUser(User u) {
        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "insert into users(id , username , email, password) values(?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, u.getId());
            ps.setString(2, u.getName());
            ps.setString(3, u.getEmail());

            String hashPassword = BCrypt.hashpw(u.getPassword(), BCrypt.gensalt(12));

            ps.setString(4,hashPassword );
            int i = ps.executeUpdate();

            return i > 0;

        } catch (SQLException e) {
            System.out.println(e);
        }
  return false;
    }

    public static User getUserData(String name, String password) {
        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "select id, email,created_date from users where username =? and password =?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
        
            if (rs.next()) {
                int id = rs.getInt("id");
                String email = rs.getString("email");
                Timestamp created_date = rs.getTimestamp("created_date");
                return new User(id, name, email, password, created_date);
            }
            

        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;

    }
    public static User getUserById(int userId) {
        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "SELECT * FROM users WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");
                Timestamp createdDate = rs.getTimestamp("created_date");
                return new User(id, username, email, password, createdDate);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public static boolean updateUser(User u) {
        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setInt(4, u.getId());
            int i = ps.executeUpdate();

            return i > 0;

        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "SELECT * FROM users";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");
                Timestamp createdDate = rs.getTimestamp("created_date");
                users.add(new User(id, username, email, password, createdDate));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return users;
    }
    public static boolean deleteUserAndAllData(int userId) {
        Connection conn = null;
        PreparedStatement psExpense = null;
        PreparedStatement psBudget = null;
        PreparedStatement psCategory = null;
        
        PreparedStatement psUser = null;
    
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false); // Transaction start
    
            String deleteExpenses = "DELETE FROM expenses WHERE u_id = ?";
            psExpense = conn.prepareStatement(deleteExpenses);
            psExpense.setInt(1, userId);
            psExpense.executeUpdate();
            
            String deleteBudgets = "DELETE FROM budgets WHERE user_id = ?";
            psBudget = conn.prepareStatement(deleteBudgets);
            psBudget.setInt(1, userId);
            psBudget.executeUpdate();
          
            String deleteCategories = "DELETE FROM category WHERE u_id = ?";
            psCategory = conn.prepareStatement(deleteCategories);
            psCategory.setInt(1, userId);
            psCategory.executeUpdate();
    
            String deleteUser = "DELETE FROM users WHERE id = ?";
            psUser = conn.prepareStatement(deleteUser);
            psUser.setInt(1, userId);
            int userDeleted = psUser.executeUpdate();
    
            conn.commit();
    
            return userDeleted > 0; 
    
        } catch (SQLException e) {
            System.out.println("Error deleting user and related data: " + e);
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
           
            try {
                if (psExpense != null) psExpense.close();
                if (psCategory != null) psCategory.close();
                if (psBudget != null) psBudget.close();
                if (psUser != null) psUser.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
    
}
