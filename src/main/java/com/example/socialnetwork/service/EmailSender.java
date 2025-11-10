package com.example.socialnetwork.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailSender {
    public static void sendEmail(String to, String subject, String body) throws MessagingException {
        String host = "smtp.gmail.com";
        final String email = "nguyenquanganh0704204@gmail.com";
        final String password = "mbge zhbb xkxj pqoe";


        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(email, password);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(email));
        message.setSubject(subject);
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setText(body);
        Transport.send(message);
    }
}
