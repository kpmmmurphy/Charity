/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import com.sun.mail.smtp.SMTPTransport;
import java.security.Security;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author doraemon
 */
public class GoogleMail {
    
    /* GMail username */
    private final String username;
    /* GMail password */
    private final String password;
    /* Receipient of the email*/
    private String recipient;
    private Properties properties;
    private SMTPTransport transport;
    
    /**
     * 
     * @param username GMail username
     * @param password GMail password
     * @param recipient email of the recipient of the message
     */
    public GoogleMail(String username, String password, String recipient) {
        this.username = username;
        this.password = password;
        this.recipient = recipient;
        properties = System.getProperties();
    }
    
    /**
     * Send email using GMail SMTP server.
     *
     * @param ccEmail CC recipient. Can be empty if there is no CC recipient
     * @param mailSubject title of the message
     * @param mailText message to be sent
     * @throws AddressException if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a MimeMessage
     */
    public void send(String ccEmail, String mailSubject, String mailText) {
        try {
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

            // Get a Properties object
            properties.setProperty("mail.smtps.host", "smtp.gmail.com");
            properties.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            properties.setProperty("mail.smtp.socketFactory.fallback", "false");
            properties.setProperty("mail.smtp.port", "465");
            properties.setProperty("mail.smtp.socketFactory.port", "465");
            properties.setProperty("mail.smtps.auth", "true");

           /*
            If set to false, the QUIT command is sent and the connection is immediately closed. If set 
            to true (the default), causes the transport to wait for the response to the QUIT command.

            ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
                    http://forum.java.sun.com/thread.jspa?threadID=5205249
                    smtpsend.java - demo program from javamail
            */
            
            properties.put("mail.smtps.quitwait", "false");

            Session session = Session.getInstance(properties, null);

            /* Create a new message */
            final MimeMessage message = new MimeMessage(session);
            /* Set the 'to' and 'from' fields */
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));

            /* Add recipients if necesssary */
            if (ccEmail.length() > 0) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
            }

            /* Set the email subject */
            message.setSubject(mailSubject);
            /* Set the email text */
            message.setText(mailText, "utf-8");
            /* Set the email date */
            message.setSentDate(new Date());

            transport = (SMTPTransport)session.getTransport("smtps");

            transport.connect("smtp.gmail.com", username, password);
            transport.sendMessage(message, message.getAllRecipients());      
            transport.close();
        } catch(AddressException addressException) {
            addressException.printStackTrace();
        } catch(MessagingException messagingException) {
            messagingException.printStackTrace();
        }
    }
}