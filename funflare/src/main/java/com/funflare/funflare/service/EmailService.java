package com.funflare.funflare.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.funflare.funflare.service.EmailService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
        public void sendVerificationEmail(String recipientEmail, String verification_token) {

            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF8");
                helper.setTo(recipientEmail);
                helper.setFrom("Silas.ga0018@gmail.com");

                String verificationLink = "http://localhost:8080/api/auth/verify?token=" + URLEncoder.encode(verification_token, StandardCharsets.UTF_8);
                String htmlContent = """
                    <h2> Welcome to funflare.com</h2>
                    <p> please verify your email by clicking the link below;</p>
                    <a href = "%s">Verify email</a>
                    <p> if you did not sign up for a funflare account please ignore this email</p>
                    """.formatted(verificationLink);

                helper.setText(htmlContent, true);
                mailSender.send(mimeMessage);


            } catch (MessagingException e) {
                throw new RuntimeException("failed to send email", e); }

        }


    }



