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
    public static void sendEmail(String toEmail, String link, String name){
        try{
            String host = "smtp.gmail.com";
            String port = "587";
            final String hostEmail = "devquery391@gmail.com";
            final String hostPassword = "passswp123456789";
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
            msg.setFrom(new InternetAddress(hostEmail,"DevQuery System","UTF-8"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject("Account Recovery - DevQuery", "UTF-8");
            String content = "We received an account recovery request on Stack Overflow for" + toEmail + ".\n"
                            + "If you initated this request, please click the link below to reset password :\n " + link + "\n\n"
                            + "If you did not initate this request , just ignore this email " +"\n\n"
                            + "Thanks you for yours support";
            msg.setText(content, "UTF-8", "text/html");
            Transport.send(msg);
            System.out.println("Send successfully");
        }catch(Exception e){
            System.out.println("Email này không tồn tại!");
            System.out.println("Xin hãy nhập lại Email");
            e.printStackTrace();
        }
    }
}
