package com.electromart.auth.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    
    // Setter injection instead of field injection with @Lazy
    private JavaMailSender mailSender;
    
    // Setter injection with @Autowired(required = false)
    @Autowired(required = false)
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        if (mailSender == null) {
            log.info("Email service configured in DEV mode - emails will be logged");
        } else {
            log.info("Email service configured with JavaMailSender");
        }
    }
    
    @Override
    public void sendEmail(String to, String subject, String body) {
        // Check at the VERY BEGINNING
        if (mailSender == null) {
            log.info("📧 [DEV EMAIL] To: {}, Subject: {}", to, subject);
            log.info("📧 [DEV EMAIL BODY]: {}", body);
            return; // CRITICAL: Return immediately
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@electromart.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (MailException e) {
            log.error("Failed to send email to: {}", to, e);
            // Don't throw - we don't want password reset to fail due to email issues
        }
    }
    
    @Override
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        // Check at the VERY BEGINNING
        if (mailSender == null) {
            log.info("📧 [DEV HTML EMAIL] To: {}, Subject: {}", to, subject);
            // Extract plain text from HTML for logging
            String plainText = htmlBody.replaceAll("<[^>]*>", "");
            if (plainText.length() > 100) {
                plainText = plainText.substring(0, 100) + "...";
            }
            log.info("📧 [DEV HTML CONTENT]: {}", plainText);
            return; // CRITICAL: Return immediately
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("no-reply@electromart.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            
            mailSender.send(message);
            log.info("HTML email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send HTML email to: {}", to, e);
        }
    }
    
    public boolean isEmailConfigured() {
        return mailSender != null;
    }
}