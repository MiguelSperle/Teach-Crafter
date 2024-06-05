package com.miguelsperle.teach_crafter.modules.users.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class EmailSenderService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    public EmailSenderService(
            final JavaMailSender javaMailSender,
            final SpringTemplateEngine springTemplateEngine
    ) {
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
    }

    @Value("${spring.mail.username}")
    private String mailUsername;

    public void sendSimpleMessage(String to, String subject, String token) {
        try {
            MimeMessage message = this.javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            Context context = new Context();
            context.setVariable("email", to);
            context.setVariable("token", token);

            String processTemplate = this.springTemplateEngine.process("resetPasswordTemplate.html", context);

            helper.setFrom(this.mailUsername);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(processTemplate, true);

            this.javaMailSender.send(message);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to send email", exception);
        }
    }
}
