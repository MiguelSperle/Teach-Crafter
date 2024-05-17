package com.miguelsperle.teach_crafter.modules.users.services;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailSenderServiceTest {
    @InjectMocks
    private EmailSenderService emailSenderService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private SpringTemplateEngine springTemplateEngine;

    @Value("${spring.mail.username}")
    private String mailUsername;

    private MimeMessage mimeMessage;
    private String to;
    private String subject;
    private String token;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        this.to = "exampleToSend@gmail.com";
        this.subject = "Test Subject";
        this.token = UUID.randomUUID().toString();
        String from = "exampleFrom@gmail.com";

        this.mimeMessage = mock(MimeMessage.class);

        when(this.javaMailSender.createMimeMessage()).thenReturn(this.mimeMessage);

        when(this.springTemplateEngine.process(any(String.class), any())).thenReturn("<html>Mocked Template</html>");

        // We are taking a private attribute called "mailUsername" on the class EmailSenderService
        Field mailUsernameField = EmailSenderService.class.getDeclaredField("mailUsername");

        // We must set "true" so can modify in a class that is not the class that created it
        mailUsernameField.setAccessible(true);

        mailUsernameField.set(this.emailSenderService, from);
        // In the first argument we are putting the class that attribute captured belongs
        // In the second argument we are changing the value of mailUsername to from attribute
    }


    @Test
    @DisplayName("Should be able to send an email")
    public void should_be_able_to_send_an_email() {
        this.emailSenderService.sendSimpleMessage(this.to, this.subject, this.token);

        verify(this.javaMailSender, atLeastOnce()).createMimeMessage();
        verify(this.javaMailSender, atLeastOnce()).send(this.mimeMessage);
        verify(this.springTemplateEngine, atLeastOnce()).process(any(String.class), any());
    }

    @Test
    @DisplayName("Should be able to throw an exception when occurs an error to send an email")
    public void should_be_able_to_throw_an_exception_when_occurs_an_error_to_send_an_email() {
        doThrow(RuntimeException.class).when(this.javaMailSender).send(this.mimeMessage);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            this.emailSenderService.sendSimpleMessage(this.to, this.subject, this.token);
        });

        String expectedErrorMessage = "Failed to send email";

        // Verify if the cause is kind of MailSendException.class
        assertInstanceOf(RuntimeException.class, exception.getCause());

        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }
}
