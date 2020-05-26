/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vishnu.utilities;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author ncreh
 */
public class MailSenderGmail {

    public static void sendWithoutAttachmentMail(final String frommail, final String password, String tomailid, String subject, String content) throws MessagingException {
        System.out.println("@@@@@@@@@@@@@" + tomailid);
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.bizmobia.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(frommail, password);
            }
        });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(frommail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(tomailid));
        message.setSubject(subject);
        message.setContent(content, "text/html; charset=utf-8");
        Transport.send(message);
        System.out.println("Done");

    }
}
