package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import database.DBConnection;

public class EmailLogDAO {
    public static boolean insertRecord(int userId, String reportType, String reportPeriod, String status) {
        String sql = "INSERT INTO email_logs (user_id, report_type, report_period, status) "
                + "VALUES (?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, userId);
            ps.setString(2, reportType);
            ps.setString(3, reportPeriod);
            ps.setString(4, status);

            int rows = ps.executeUpdate();
            return rows > 0; // true if inserted
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
