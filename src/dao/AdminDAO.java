package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.DBConnection;
import model.Admin;
import java.security.MessageDigest;

import utils.Session;

public class AdminDAO {
    public static boolean isAdmin(String adminName, String password) {
        try {
            Connection conn = DBConnection.getDBConnection();
            String hashPassword = sha256(password);
            String sql = "SELECT * FROM admins WHERE admin_name = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, adminName);
            ps.setString(2, hashPassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Session.setCurrentUser(rs.getInt("admin_id"));
                return true;
            }

        } catch (SQLException e) {
            System.out.println(e);

        }
        return false;

    }

    public static Admin getAdminById(int adminId) {
        Admin admin = null;
        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "SELECT * FROM admins WHERE admin_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, adminId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                admin = new Admin(
                        rs.getInt("admin_id"),
                        rs.getString("admin_name"),
                        rs.getString("email"),
                        rs.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return admin;
    }

    public static boolean updateAdmin(Admin a) {
        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "UPDATE admins SET admin_name = ?,email = ?, password = ? WHERE admin_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, a.getAdminName());
            ps.setString(2, a.getAdminEmail());
            ps.setString(3, a.getPassword());
            ps.setInt(4, a.getAdminId());
            int i = ps.executeUpdate();

            return i > 0;

        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public static int totalUsers() {
        int totalUsers = 0;
        try {
            Connection conn = DBConnection.getDBConnection();
            String sql = "SELECT COUNT(*) AS total FROM users";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalUsers = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return totalUsers;
    }

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hash = md.digest(input.getBytes("UTF-8"));

            // Convert bytes → Hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}