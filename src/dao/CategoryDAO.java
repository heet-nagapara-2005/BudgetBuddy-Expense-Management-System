package dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CategoryDAO {
    private static int totalCategories;

    public static int getTotalCategories() {
        return totalCategories;
    }

    public static boolean addCategory(Category c) {
        
        try {
            Connection conn = DBConnection.getDBConnection();
            String cleanCategory = c.getName().trim();
            String matchCategory = "SELECT * FROM category WHERE c_name = ? and u_id = ?";
            PreparedStatement matchPs = conn.prepareStatement(matchCategory);
            matchPs.setString(1, cleanCategory); // Use trimmed name for matching
            matchPs.setInt(2, c.getU_id()); 
            ResultSet matchRs = matchPs.executeQuery();
            if (matchRs.next()) {
                // Category already exists for this user
                return false;
            }

            String sql = "INSERT INTO category (c_id,u_id,c_name) VALUES (?,?,?)";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, c.getId());
            ps.setInt(2, c.getU_id());
            ps.setString(3, cleanCategory); // Use trimmed name for insertion
            int i = ps.executeUpdate();
            return i > 0;

        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public static List<Category> getAllCategories(int userId) {
        List<Category> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM category where u_id =?";
            Connection conn = DBConnection.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId); // replace with current logged-in user id
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category c = new Category(rs.getInt("c_id"), rs.getInt("u_id"), rs.getString("c_name"),
                        rs.getTimestamp("created_date"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    // ✅ Delete method add kiya
    public static boolean deleteCategory(int id, int userId) {
        try {
            String sql = "DELETE FROM category WHERE c_id = ? and u_id =?";
            Connection conn = DBConnection.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setInt(2, userId); // replace with current logged-in user id
            int i = ps.executeUpdate();
            return i > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public static int totalCategories(int userId) {
        int total = 0;
        try {
            String sql = "SELECT COUNT(*) FROM category WHERE u_id =?";
            Connection conn = DBConnection.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId); // replace with current logged-in user id
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return total;
    }

  

}
