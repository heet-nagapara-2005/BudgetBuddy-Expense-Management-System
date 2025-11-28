package utils;

import jakarta.mail.PasswordAuthentication;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.mail.Authenticator;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailUtil {
    
    private static Session session2;
    private static Transport transport2;
    private static final String EMAIL_REGEX = 
    "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }


   public static void sendMail(String to, String from, String subject, String msg) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("budgetbuddy6353@gmail.com", "pamu cddj ejoq fszs");
            }
        });

        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(from);
            message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(msg);
            Transport.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void initMailConfig() {
       try {
            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true");

            session2 = Session.getInstance(prop, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        "budgetbuddy6353@gmail.com", // Gmail
                        "pamu cddj ejoq fszs"       // App Password
                    );
                }
            });

            session2.setDebug(true);

            // Transport object create and connect once
            transport2 = session2.getTransport("smtp");
            transport2.connect("smtp.gmail.com", "budgetbuddy6353@gmail.com", "pamu cddj ejoq fszs");


        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }

    public static boolean sendMultipleEmails(String to, String from, String subject, String msg) {
        try {
            MimeMessage message = new MimeMessage(session2);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(msg);

            // Reuse same transport connection
            transport2.sendMessage(message, message.getAllRecipients());
            return true; // Email sent successfully
           // System.out.println("Email sent successfully to " + to);

        } catch (Exception e) {

            e.printStackTrace();
            return false; // Failed to send email
        }
    }

    public static void closeMailConfig() {
        try {
            if (transport2 != null) {
                transport2.close();
                //System.out.println("SMTP Connection closed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
