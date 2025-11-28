
 import jakarta.mail.PasswordAuthentication;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;


public class TestMailCode {
    public static void main(String args[]){
        String msg = "hello,manali how are you";
        String subject = "Test Email";
        String to = "manalisitapara10@gmail.com";
        String from = "budgetbuddy6353@gmail.com";
        sendMail(to, from, subject, msg);

    }
    private static  void sendMail(String to, String from, String subject, String msg) {
          Properties prop = new Properties();
          prop.put("mail.smtp.host","smtp.gmail.com");
          prop.put("mail.smtp.port","587");
            prop.put("mail.smtp.auth","true");
            prop.put("mail.smtp.starttls.enable","true");

            Session  session = Session.getInstance(prop, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication(){
                    return new PasswordAuthentication("budgetbuddy6353@gmail.com","pamu cddj ejoq fszs");
                }
            });

            session.setDebug(true);
            MimeMessage message = new MimeMessage(session);
            try{
                message.setFrom(from);
                message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
                message.setSubject(subject);
                message.setText(msg);
                Transport.send(message);
                System.out.println("Email sent successfully to " + to);
            }
            catch(Exception e){
                e.printStackTrace();
            }

            
    }
}
