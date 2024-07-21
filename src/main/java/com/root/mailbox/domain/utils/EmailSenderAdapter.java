package com.root.mailbox.domain.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class EmailSenderAdapter {
    private static final Map<String, String> TEMPLATES = new HashMap<>();
    private final SimpleMailMessage mailMessage = new SimpleMailMessage();

    private final JavaMailSender mailSender;

    static {
        TEMPLATES.put("forgot-password", "<html><head><title>Mail-Box App</title></head><body style=\"text-align: center\"><h1>Hey! Here''s your new password</h1><div style=\"text-align: left\"> <p>Hello {0}! Did you request a new password?</p> <p>This is your new provisory password: {1}</p> <p><strong>Remember to change it on your first access!</strong></p> <small>If you didn''t request a new password, go on \"Forgot Password\" page, request a new password then change it on your profile area after login.</small></div></body></html>");
    }

    public void forgotPasswordMail(String to, String subject, List<String> params, String templateName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            mimeMessage.setSubject(subject);

            helper.setTo(to);

            String template = TEMPLATES.get(templateName);

            String body = MessageFormat.format(template, params.get(0), params.get(1));

            helper.setText(body, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            log.error("Error while sending forgot password e-mail...", ex);
            throw new RuntimeException("Error while sending forgot password e-mail...");
        }
    }
}
