package com.example.sacco_core_banking.services;

import java.time.Year;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

/**
 * Sends transactional emails via Thymeleaf-rendered HTML with the SmoothSurf logo
 * embedded inline (cid:logo) so it renders without depending on external image
 * hosting. Failures are logged and swallowed — a broken mail server must never fail
 * registration or OTP issuance, both of which already succeeded in the database by
 * the time we try to send.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final String LOGO_PATH = "email/smoothsurf-logo.png";

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${app.mail.enabled}")
    private boolean mailEnabled;
    @Value("${app.mail.from}")
    private String fromAddress;
    @Value("${app.mail.from-name}")
    private String fromName;

    public void sendWelcomeEmail(String to, String recipientName) {
        Context context = new Context();
        context.setVariable("recipientName", recipientName);
        context.setVariable("text", "Thank you for registering with SmoothSurf Sacco. "
                + "Your account has been created and is now pending admin approval.");
        context.setVariable("senderName", fromName);
        context.setVariable("copyrightYear", Year.now().getValue());

        send(to, "Your SmoothSurf Sacco account has been created", "email/welcome-email", context);
    }

    public void sendInvitationEmail(String to, String recipientName, String tempPassword, String activationLink) {
        Context context = new Context();
        context.setVariable("recipientName", recipientName);
        context.setVariable("email", to);
        context.setVariable("tempPassword", tempPassword);
        context.setVariable("activationLink", activationLink);
        context.setVariable("senderName", fromName);
        context.setVariable("copyrightYear", Year.now().getValue());

        send(to, "You've been invited to SmoothSurf Sacco — Activate your account", "email/invitation-email", context);
    }

    public void sendOtpEmail(String to, String recipientName, String otp, int expiryMinutes) {
        Context context = new Context();
        context.setVariable("recipientName", recipientName);
        context.setVariable("otp", otp);
        context.setVariable("expiryMinutes", expiryMinutes);
        context.setVariable("senderName", fromName);
        context.setVariable("copyrightYear", Year.now().getValue());

        send(to, "Your SmoothSurf Sacco verification code", "email/otp-email", context);
    }

    private void send(String to, String subject, String template, Context context) {
        if (!mailEnabled) {
            logger.info("Mail disabled (app.mail.enabled=false) — skipping email to {} [{}]", to, subject);
            return;
        }

        try {
            String html = templateEngine.process(template, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            helper.addInline("logo", new ClassPathResource(LOGO_PATH));

            mailSender.send(message);
        } catch (Exception ex) {
            logger.warn("Failed to send email to {} [{}]: {}", to, subject, ex.getMessage());
        }
    }
}
