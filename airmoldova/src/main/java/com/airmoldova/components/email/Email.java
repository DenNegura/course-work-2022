package com.airmoldova.components.email;


import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.io.File;

public class Email {


    private JavaMailSender emailSender;
    public Email(String document, String from, String to, String subj, String msg) {
        try {
            emailSender = new EmailService().getJavaMailSender();
            MimeMessage message = emailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subj);
            helper.setText(msg);

            FileSystemResource file = new FileSystemResource(new File(document));
            helper.addAttachment("ticket.pdf", file);

            emailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public Email(String to, String subject, String text) {
//        emailSender = new EmailService().getJavaMailSender();
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("noreply@baeldung.com");
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(text);
//        emailSender.send(message);
//    }

}