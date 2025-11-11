// src/main/java/com/funflare/funflare/service/EmailService.java
package com.funflare.funflare.service;

import com.funflare.funflare.model.Purchases;
import com.funflare.funflare.model.TicketPurchase;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ──────────────────────────────────────────────────────────────
    //  VERIFICATION EMAIL (UNCHANGED)
    // ──────────────────────────────────────────────────────────────
    public void sendVerificationEmail(String recipientEmail, String verification_token) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(recipientEmail);
            helper.setFrom("Silas.ga0018@gmail.com");
            helper.setSubject("Verify Your FunFlare Account");

            String verificationLink = "http://localhost:8080/api/auth/verify?token=" +
                    URLEncoder.encode(verification_token, StandardCharsets.UTF_8);

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 40px auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; background: #f9f9f9;">
                    <h1 style="color: #f97316; text-align: center;">FunFlare</h1>
                    <h2>Welcome to FunFlare!</h2>
                    <p>Please verify your email by clicking the button below:</p>
                    <p style="text-align: center;">
                        <a href="%s" style="padding: 15px 30px; background: #f97316; color: white; text-decoration: none; border-radius: 8px; font-weight: bold;">Verify Email Now</a>
                    </p>
                    <p>If you didn't sign up, please ignore this email.</p>
                    <hr>
                    <p style="color: #666; font-size: 12px;">FunFlare - Nairobi's #1 Event Platform</p>
                </div>
                """.formatted(verificationLink);

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            logger.info("Verification email sent to {}", recipientEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send verification email to {}", recipientEmail, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  TICKET EMAIL WITH PDF + QR (100% SAFE – NO CRASHES)
    // ──────────────────────────────────────────────────────────────
    public void sendTicketEmail(
            String recipientEmail,
            String guestName,
            Purchases purchase,
            List<byte[]> pdfAttachments,
            List<String> pdfNames
    ) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setFrom("Silas.ga0018@gmail.com");
            helper.setSubject("Your FunFlare Tickets - Purchase #" + purchase.getId());

            // SAFELY GET EVENT NAME – NO INDEX OUT OF BOUNDS
            String eventName = "FunFlare Event";
            List<TicketPurchase> tickets = purchase.getTicketPurchases();
            if (tickets != null && !tickets.isEmpty()) {
                TicketPurchase firstTicket = tickets.get(0);
                if (firstTicket.getTicket() != null && firstTicket.getTicket().getEvent() != null) {
                    eventName = firstTicket.getTicket().getEvent().getName();
                }
            }

            String htmlBody = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 40px auto; padding: 30px; border: 2px solid #f97316; border-radius: 15px; background: linear-gradient(to bottom, #fff8f0, #ffffff); box-shadow: 0 10px 20px rgba(249, 115, 22, 0.1);">
                    <h1 style="color: #f97316; text-align: center; font-size: 32px; margin-bottom: 10px;">FunFlare</h1>
                    <p style="text-align: center; color: #666; font-size: 14px; margin-bottom: 30px;">Nairobi's #1 Event Ticketing Platform</p>
                    
                    <h2 style="color: #333;">Hello %s!</h2>
                    <p>Thank you for your purchase! Your tickets are ready.</p>
                    
                    <div style="background: #f97316; color: white; padding: 15px; border-radius: 10px; text-align: center; margin: 20px 0;">
                        <p style="margin: 5px 0; font-size: 16px;"><strong>Purchase ID:</strong> #%d</p>
                        <p style="margin: 5px 0; font-size: 18px;"><strong>Total Paid:</strong> KES %.2f</p>
                    </div>
                    
                    <p><strong>Event:</strong> %s</p>
                    <p><strong>Tickets Attached:</strong> %d PDF(s) with unique QR code</p>
                    
                    <div style="background: #fff3e0; padding: 15px; border-left: 5px solid #f97316; margin: 25px 0; border-radius: 0 8px 8px 0;">
                        <p style="margin: 0; font-weight: bold; color: #d35400;">
                            Present the QR code at the gate for instant entry.
                        </p>
                        <p style="margin: 10px 0 0; color: #666; font-size: 14px;">
                            No printing needed — show from your phone!
                        </p>
                    </div>
                    
                    <hr style="border: 1px dashed #ddd; margin: 30px 0;">
                    
                    <p style="color: #666; font-size: 13px; text-align: center;">
                        Need help? WhatsApp <strong>+254707230237</strong><br>
                        FunFlare Team • Nairobi, Kenya • 2025
                    </p>
                </div>
                """.formatted(
                    guestName != null ? guestName : "Guest",
                    purchase.getId(),
                    purchase.getTotalAmount(),
                    eventName,
                    pdfAttachments.size()
            );

            helper.setText(htmlBody, true);

            // Attach all PDFs
            for (int i = 0; i < pdfAttachments.size(); i++) {
                ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfAttachments.get(i), "application/pdf");
                String fileName = pdfNames.get(i);
                helper.addAttachment(fileName, dataSource);
                logger.debug("Attached PDF: {} ({} bytes)", fileName, pdfAttachments.get(i).length);
            }

            mailSender.send(mimeMessage);
            logger.info("TICKET EMAIL SENT SUCCESSFULLY → {} | {} ticket(s) | Purchase #{} | Event: {}",
                    recipientEmail, pdfAttachments.size(), purchase.getId(), eventName);

        } catch (Exception e) {
            // DO NOT CRASH THE PURCHASE — JUST LOG
            logger.error("FAILED TO SEND TICKET EMAIL for purchase {} to {} | Error: {}",
                    purchase.getId(), recipientEmail, e.getMessage(), e);
            System.err.println("EMAIL FAILED BUT PURCHASE STILL COMPLETED: " + e.getMessage());
            // Do NOT rethrow — let user get ticket even if email fails
        }
    }
}