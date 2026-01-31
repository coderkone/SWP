/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author Asus
 */
public class EmailUtils {
    public static void sendEmail(String toEmail, String subject, String body){
        try{
            String host = "smtp.gmail.com";
            String port = "587";
            final String hostEmail = "admin@devquery.com";
            final String hostPassword = "passswpproject00";
            Properties pro = new Properties();
            pro.put("mail.smtp.host", host);
            pro.put("mail.smtp.port", port);
            pro.put("mail.smtp.auth", "true");
            pro.put("mail.smtp.STARTTLS.enable", "true");
            Authenticator auth = new Authenticator(){
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(hostEmail, hostPassword); 
                }
            };
            Session session = Session.getInstance(pro, auth);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(hostEmail,"DevQuery System","utf-8"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject(subject, "utf-8");
            msg.setText(body, "utf-8", "html");
            Transport.send(msg);
        }catch(Exception e){
            System.out.println("Email này không tồn tại!");
            System.out.println("Xin hãy nhập lại Email");
            e.printStackTrace();
        }
    }
}
