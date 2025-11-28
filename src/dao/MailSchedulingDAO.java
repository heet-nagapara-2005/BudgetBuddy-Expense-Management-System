package dao;

import java.util.ArrayList;
import java.util.List;


import model.GenerateReport;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MailSchedulingDAO {
   
    public static List<GenerateReport> fetchReportsByUserId(int userId) {
        List<GenerateReport> reports = new ArrayList<>();
        String sql = "SELECT * FROM generated_reports WHERE user_id = ?";

        try {
            Connection conn = DBConnection.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                GenerateReport report = new GenerateReport(
                        rs.getInt("report_id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("report_type"),
                        rs.getString("report_period"),
                        rs.getString("all_categories_amount"),
                        rs.getString("highest_amount_category"),
                        rs.getString("lowest_amount_category"),
                        rs.getDouble("average_spending"),
                        rs.getInt("total_transactions"),
                        rs.getTimestamp("created_at"));
                reports.add(report);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching reports by user ID: " + e.getMessage());
        }
        return reports;
    }

    public static List<GenerateReport> fetchAllUserReports(String reportType, String reportPeriod) {
        List<GenerateReport> reports = new ArrayList<>();
        String sql = "SELECT * FROM generated_reports WHERE report_type = ? AND report_period = ?";

        try {
            Connection conn = DBConnection.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, reportType);
            ps.setString(2, reportPeriod);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                GenerateReport report = new GenerateReport(
                        rs.getInt("report_id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("report_type"),
                        rs.getString("report_period"),
                        rs.getString("all_categories_amount"),
                        rs.getString("highest_amount_category"),
                        rs.getString("lowest_amount_category"),
                        rs.getDouble("average_spending"),
                        rs.getInt("total_transactions"),
                        rs.getTimestamp("created_at"));
                       
                   reports.add(report);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all user reports for month: " + e.getMessage());
        }
        return reports;
    }   

    
    public static List<GenerateReport> fetchPendingUserReports(String reportType, String reportPeriod) {
        List<GenerateReport> reports = new ArrayList<>();
        String sql = "SELECT gr.* " +
                     "FROM generated_reports gr " +
                     "WHERE gr.report_type = ? AND gr.report_period = ? " +
                     "AND NOT EXISTS ( " +
                     "    SELECT 1 FROM email_logs el " +
                     "    WHERE el.user_id = gr.user_id " +
                     "      AND el.report_type = gr.report_type " +
                     "      AND el.report_period = gr.report_period " +
                     "      AND el.status = 'sent' " +
                     ")";
   
    
        try {
            Connection conn = DBConnection.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, reportType);
            ps.setString(2, reportPeriod);
            ResultSet rs = ps.executeQuery();

           

    
            while (rs.next()) {
                GenerateReport report = new GenerateReport(
                        rs.getInt("report_id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("report_type"),
                        rs.getString("report_period"),
                        rs.getString("all_categories_amount"),
                        rs.getString("highest_amount_category"),
                        rs.getString("lowest_amount_category"),
                        rs.getDouble("average_spending"),
                        rs.getInt("total_transactions"),
                        rs.getTimestamp("created_at")
                );
                
                reports.add(report);
            }
          

        } catch (SQLException e) {
            System.out.println("Error fetching pending user reports: " + e.getMessage());
        }
        return reports;
    }
    

}
