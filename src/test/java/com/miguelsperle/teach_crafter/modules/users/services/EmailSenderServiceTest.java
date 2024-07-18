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

    private String to;
    private String subject;
    private String token;
    private String from;

    @BeforeEach
    public void setUp() {
        this.to = "exampleToSend@gmail.com";
        this.subject = "Test Subject";
        this.token = "aB3dE6Gh7iJkL9Mn0pQrStUv";
        this.from = "exampleFrom@gmail.com";
    }


    @Test
    @DisplayName("Should be able to send an email")
    public void should_be_able_to_send_an_email() throws NoSuchFieldException, IllegalAccessException {
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(this.javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        when(this.springTemplateEngine.process(any(String.class), any())).thenReturn("<html>Mocked Template</html>");

        // We are taking a private attribute called "mailUsername" on the class EmailSenderService
        Field mailUsernameField = EmailSenderService.class.getDeclaredField("mailUsername");

        // We must set "true" so can modify in a class that is not the class that created it
        mailUsernameField.setAccessible(true);

        mailUsernameField.set(this.emailSenderService, this.from);
        // In the first argument we are putting the class that attribute captured belongs
        // In the second argument we are changing the value of mailUsername to from attribute

        this.emailSenderService.sendSimpleMessage(this.to, this.subject, this.token);

        verify(this.javaMailSender, atLeastOnce()).createMimeMessage();
        verify(this.springTemplateEngine, atLeastOnce()).process(anyString(), any());
        verify(this.javaMailSender, atLeastOnce()).send(mimeMessage);
    }

    @Test
    @DisplayName("Should be able to throw an exception when occurs an error to send an email")
    public void should_be_able_to_throw_an_exception_when_occurs_an_error_to_send_an_email() {
        when(this.javaMailSender.createMimeMessage()).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            this.emailSenderService.sendSimpleMessage(this.to, this.subject, this.token);
        });

        String expectedErrorMessage = "Failed to send email";

        // Verify if the cause is kind of RuntimeException.class
        assertInstanceOf(RuntimeException.class, exception.getCause());

        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }
}
