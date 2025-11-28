package model;

import java.sql.Timestamp;

public class EmailLogs {
    
    private int logId;
    private int userId;
    private String reportType;
    private String reportPeriod;
    private Timestamp sentAt;
    private String status;

    // Default Constructor
    public EmailLogs() {
    }

    // Parameterized Constructor
    public EmailLogs(int logId, int userId, String reportType, String reportPeriod, Timestamp sentAt, String status) {
        this.logId = logId;
        this.userId = userId;
        this.reportType = reportType;
        this.reportPeriod = reportPeriod;
        this.sentAt = sentAt;
        this.status = status;
    }

    // Getters and Setters
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
