package mailschedular;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import model.*;
import dao.*;
import utils.EmailUtil;

public class AutoMailSchedular {
    List<User> users; 

    public static void main(String[] args) {
        System.out.println("Auto Mail Scheduler Started...");
        long startTime = System.currentTimeMillis();
        long stopTime = 60*12*1000; // 12 minutes in milliseconds
        // Timer banate hain jo repeated task run kare
        Timer timer = new Timer();
        
        // TimerTask define karo
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    if( System.currentTimeMillis() - startTime >= stopTime) {
                        System.out.println("Stopping Auto Mail Scheduler after 10 minutes.");
                        timer.cancel(); // Stop the task after 10 minutes
                        timer.purge(); 
                        return;
                    }
                    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MM-yyyy");
                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy");
                 //   String previousMonthYear = LocalDate.now().minusMonths(1).format(formatter1);
                   // String previousYear = LocalDate.now().minusYears(1).format(formatter2);
                   String previousMonthYear = LocalDate.now().format(formatter1).replace("-","/");
                   String previousYear = LocalDate.now().format(formatter2);
                 

                   // List<GenerateReport> montlyReports = MailSchedulingDAO.fetchAllUserReports("monthly", previousMonthYear);
                    List<GenerateReport> montlyReports = MailSchedulingDAO.fetchPendingUserReports("monthly", previousMonthYear);

                    List<String> generatedMonthlyReports = ExpenseReportGenerator.reportGenerating(montlyReports);
                  

                    //List<GenerateReport> yearlyReports = MailSchedulingDAO.fetchAllUserReports("yearly", previousYear);
                    List<GenerateReport> yearlyReports = MailSchedulingDAO.fetchPendingUserReports("yearly", previousYear);

                    List<String> generatedYearlyReports = ExpenseReportGenerator.reportGenerating(yearlyReports);
                   

                    EmailUtil.initMailConfig();
                    System.out.println("Email configuration initialized.");
                   
                    if (generatedMonthlyReports == null || generatedMonthlyReports.isEmpty()) {
                        System.out.println("No monthly reports to send.");
                    } else {
                        for (GenerateReport reportInfo : montlyReports) {
                          
                            String to = reportInfo.getEmail();
                            int userId = reportInfo.getUserId();
                            String reportType = reportInfo.getReportType();
                            String reportPeriod = reportInfo.getReportPeriod();

                            for (String report : generatedMonthlyReports) {
                               
                                boolean mailStatus = EmailUtil.sendMultipleEmails(
                                        to,
                                        "budgetbuddy6353@gmail.com",
                                        "Your Monthly Expense Report – BudgetBuddy",
                                        report
                                );
                                if (mailStatus) {
                                    EmailLogDAO.insertRecord(userId, reportType, reportPeriod, "sent");
                                } else {
                                    EmailLogDAO.insertRecord(userId, reportType, reportPeriod, "failed");
                                }
                            }
                        }
                    }

                    if (generatedYearlyReports == null || generatedYearlyReports.isEmpty()) {
                        System.out.println("No monthly reports to send.");
                    } else {
                        for (GenerateReport reportInfo : yearlyReports) {
                            String to = reportInfo.getEmail();
                            int userId = reportInfo.getUserId();
                            String reportType = reportInfo.getReportType();
                            String reportPeriod = reportInfo.getReportPeriod();

                            for (String report : generatedYearlyReports) {
                                boolean mailStatus = EmailUtil.sendMultipleEmails(
                                        to,
                                        "budgetbuddy6353@gmail.com",
                                        "Your Yearly Expense Report – BudgetBuddy",
                                        report
                                );
                                if (mailStatus) {
                                    EmailLogDAO.insertRecord(userId, reportType, reportPeriod, "sent");
                                } else {
                                    EmailLogDAO.insertRecord(userId, reportType, reportPeriod, "failed");
                                }
                            }
                        }
                    }
                    EmailUtil.closeMailConfig();
                } catch (Exception e) {
                    System.out.println("Error sending email: " + e.getMessage());
                }
            }
        };

        // Schedule karo: delay (ms), interval (ms)
        long delay = 0; // start immediately
        long interval = 4 * 60 * 1000; // 15 minutes in milliseconds

        timer.scheduleAtFixedRate(task, delay, interval);
    }
}
