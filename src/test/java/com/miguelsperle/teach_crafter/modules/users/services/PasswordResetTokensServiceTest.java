package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetTokens.CreatePasswordResetTokenDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetTokens.ResetPasswordUserNotLoggedDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.PasswordResetTokensEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.exceptions.ActivePasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.exceptions.ExpiredPasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.PasswordResetTokensRepository;

import com.miguelsperle.teach_crafter.utils.unit.mocks.PasswordResetTokensEntityCreator;
import com.miguelsperle.teach_crafter.utils.unit.mocks.UsersEntityCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordResetTokensServiceTest {
    @InjectMocks
    private PasswordResetTokensService passwordResetTokenService;

    @Mock
    private PasswordResetTokensRepository passwordResetTokensRepository;

    @Mock
    private UsersService usersService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailSenderService emailSenderService;

    @Test
    @DisplayName("User not logged should be able to create a password reset token to reset their password")
    public void user_not_logged_should_be_able_to_create_a_password_reset_token_to_reset_their_password() {
        when(this.usersService.getUserByEmail(any())).thenReturn(Optional.of(UsersEntityCreator.createValidAuthenticatedUsersEntity()));

        when(this.passwordResetTokensRepository.save(any(PasswordResetTokensEntity.class))).thenReturn(PasswordResetTokensEntityCreator.createPasswordResetTokensEntityToBeSaved());

        CreatePasswordResetTokenDTO createPasswordResetTokenDTO = new CreatePasswordResetTokenDTO(UsersEntityCreator.createValidAuthenticatedUsersEntity().getEmail());

        PasswordResetTokensEntity newPasswordResetToken = this.passwordResetTokenService.createPasswordResetToken(createPasswordResetTokenDTO);

        verify(this.emailSenderService).sendSimpleMessage(any(), any(), any());

        assertNotNull(newPasswordResetToken.getId());
        assertThat(newPasswordResetToken).hasFieldOrProperty("id");
    }

    @Test
    @DisplayName("User not logged should not be able to create a password reset token to reset their password if one already exists")
    public void user_not_logged_should_be_able_to_create_a_password_reset_token_to_reset_their_password_if_one_already_exists() {
        when(this.usersService.getUserByEmail(any())).thenReturn(Optional.of(UsersEntityCreator.createValidAuthenticatedUsersEntity()));

        PasswordResetTokensEntity passwordResetToken = PasswordResetTokensEntityCreator.createValidPasswordResetTokensEntity();
        passwordResetToken.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        passwordResetToken.setExpiresIn(this.genExpirationDate());

        when(this.passwordResetTokensRepository.findByUsersEntityId(any())).thenReturn(Optional.of(passwordResetToken));

        CreatePasswordResetTokenDTO createPasswordResetTokenDTO = new CreatePasswordResetTokenDTO(UsersEntityCreator.createValidAuthenticatedUsersEntity().getEmail());

        ActivePasswordResetTokenException exception = assertThrows(ActivePasswordResetTokenException.class, () -> {
            this.passwordResetTokenService.createPasswordResetToken(createPasswordResetTokenDTO);
        });

        verify(this.emailSenderService).sendSimpleMessage(any(), any(), any());

        String expectedErrorMessage = "You have an active password reset token. Please check your email to continue with password recovery";

        assertInstanceOf(ActivePasswordResetTokenException.class, exception);
        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }


    @Test
    @DisplayName("Should be able to delete expired password reset token")
    public void should_be_able_to_delete_expired_password_reset_token() {
        when(this.usersService.getUserByEmail(any())).thenReturn(Optional.of(UsersEntityCreator.createValidAuthenticatedUsersEntity()));

        PasswordResetTokensEntity expiredPasswordResetToken = PasswordResetTokensEntityCreator.createValidPasswordResetTokensEntity();
        expiredPasswordResetToken.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        expiredPasswordResetToken.setExpiresIn(new Date(System.currentTimeMillis() - 1000));
        // We are ensuring that the token will expire 1 second before the current moment.

        when(this.passwordResetTokensRepository.findByUsersEntityId(any())).thenReturn(Optional.of(expiredPasswordResetToken));

        CreatePasswordResetTokenDTO createPasswordResetTokenDTO = new CreatePasswordResetTokenDTO(UsersEntityCreator.createValidAuthenticatedUsersEntity().getEmail());

        this.passwordResetTokenService.createPasswordResetToken(createPasswordResetTokenDTO);

        verify(this.passwordResetTokensRepository).deleteById(expiredPasswordResetToken.getId());
    }

    @Test
    @DisplayName("User not logged should be able to reset their password")
    public void user_not_logged_should_be_able_to_reset_their_password() {
        PasswordResetTokensEntity passwordResetToken = PasswordResetTokensEntityCreator.createValidPasswordResetTokensEntity();
        passwordResetToken.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        passwordResetToken.setExpiresIn(this.genExpirationDate());

        when(this.passwordResetTokensRepository.findByToken(any())).thenReturn(Optional.of(passwordResetToken));

        when(this.usersService.getUserById(any())).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        String mockHashPassword = "$Kt$A.x5y8ZTgRp5b.Y5cX2tz4iQPj9vY0OK3h4df62NVO9fc720sZ";

        when(this.passwordEncoder.encode(any())).thenReturn(mockHashPassword);

        ResetPasswordUserNotLoggedDTO resetPasswordUserNotLoggedDTO = new ResetPasswordUserNotLoggedDTO(UsersEntityCreator.createUsersEntityToUpdatePassword().getPassword(), passwordResetToken.getToken());

        this.passwordResetTokenService.resetPasswordUserNotLogged(resetPasswordUserNotLoggedDTO);

        // Capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.passwordResetTokensRepository).deleteById(passwordResetToken.getId());

        // Verify if the method save was called with a specific argument
        verify(this.usersService).save(userCaptor.capture());

        assertEquals(mockHashPassword, userCaptor.getValue().getPassword());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("User not logged should not be able to reset their password if the token is expired")
    public void user_not_logged_should_not_be_able_to_reset_their_password_if_the_token_is_expired() {
        PasswordResetTokensEntity passwordResetToken = PasswordResetTokensEntityCreator.createValidPasswordResetTokensEntity();
        passwordResetToken.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        passwordResetToken.setExpiresIn(new Date(System.currentTimeMillis() - 1000));
        // We are ensuring that the token will expire 1 second before the current moment.

        when(this.passwordResetTokensRepository.findByToken(any())).thenReturn(Optional.of(passwordResetToken));

        ResetPasswordUserNotLoggedDTO resetPasswordUserNotLoggedDTO = new ResetPasswordUserNotLoggedDTO(UsersEntityCreator.createUsersEntityToUpdatePassword().getPassword(), passwordResetToken.getToken());

        ExpiredPasswordResetTokenException exception = assertThrows(ExpiredPasswordResetTokenException.class, () -> {
            this.passwordResetTokenService.resetPasswordUserNotLogged(resetPasswordUserNotLoggedDTO);
        });

        verify(this.passwordResetTokensRepository).deleteById(passwordResetToken.getId());

        String expectedErrorMessage = "The password reset token has already expired. Please make the process again";

        assertInstanceOf(ExpiredPasswordResetTokenException.class, exception);
        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    private Date genExpirationDate() {
        long expiration = 15 * 60 * 1000;  // 15 minutes in milliseconds

        Instant now = Instant.now();

        Instant expirationTime = now.plus(expiration, ChronoUnit.MILLIS);  // // Add millis to current moment

        return Date.from(expirationTime);  // Convert Instant to date
    }
}
