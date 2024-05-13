package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetToken.CreatePasswordResetTokenDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetToken.ResetPasswordUserNotLoggedDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.PasswordResetTokenEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.ActivePasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.ExpiredPasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.PasswordResetTokenRepository;
import com.miguelsperle.teach_crafter.utils.mappers.PasswordResetTokenMapper;
import com.miguelsperle.teach_crafter.utils.mocks.PasswordResetTokenEntityCreator;
import com.miguelsperle.teach_crafter.utils.mocks.UsersEntityCreator;
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
public class PasswordResetTokenServiceTest {
    @InjectMocks
    private PasswordResetTokenService passwordResetTokenService;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UsersService usersService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailSenderService emailSenderService;

    @Test
    @DisplayName("User not logged should be able to create a password reset token to reset their password")
    public void user_not_logged_should_be_able_to_create_a_password_reset_token_to_reset_their_password(){
        when(this.usersService.getUserByEmail(any())).thenReturn(Optional.of(UsersEntityCreator.createValidAuthenticatedUsersEntity()));

        when(this.passwordResetTokenRepository.findByUsersEntityId(any())).thenReturn(Optional.empty());

        when(this.passwordResetTokenRepository.save(any(PasswordResetTokenEntity.class))).thenReturn(PasswordResetTokenEntityCreator.createPasswordResetTokenToBeSaved());

        CreatePasswordResetTokenDTO convertedToCreatePasswordResetTokenDTO = PasswordResetTokenMapper.toConvertCreatePasswordResetTokenDTO(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        PasswordResetTokenEntity newPasswordResetToken = this.passwordResetTokenService.createPasswordResetToken(convertedToCreatePasswordResetTokenDTO);

        verify(this.emailSenderService).sendSimpleMessage(any(), any(), any());

        assertNotNull(newPasswordResetToken.getId());
        assertThat(newPasswordResetToken).hasFieldOrProperty("id");
    }

    @Test
    @DisplayName("User not logged should not be able to create a password reset token to reset their password if one already exists")
    public void user_not_logged_should_be_able_to_create_a_password_reset_token_to_reset_their_password_if_one_already_exists(){
        when(this.usersService.getUserByEmail(any())).thenReturn(Optional.of(UsersEntityCreator.createValidAuthenticatedUsersEntity()));

        PasswordResetTokenEntity passwordResetToken = PasswordResetTokenEntityCreator.createValidPasswordResetToken();
        passwordResetToken.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        passwordResetToken.setExpiresIn(this.genExpirationDate());

        when(this.passwordResetTokenRepository.findByUsersEntityId(any())).thenReturn(Optional.of(passwordResetToken));

        CreatePasswordResetTokenDTO convertedToCreatePasswordResetTokenDTO = PasswordResetTokenMapper.toConvertCreatePasswordResetTokenDTO(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        ActivePasswordResetTokenException exception = assertThrows(ActivePasswordResetTokenException.class, () -> {
            this.passwordResetTokenService.createPasswordResetToken(convertedToCreatePasswordResetTokenDTO);
        });

        verify(this.emailSenderService).sendSimpleMessage(any(), any(), any());

        String resultMessage = "You have an active password reset token. Please check your email to continue with password recovery.";

        assertEquals(exception.getMessage(), resultMessage);
    }


    @Test
    @DisplayName("Should be able to delete expired password reset token")
    public void should_be_able_to_delete_expired_password_reset_token(){
        when(this.usersService.getUserByEmail(any())).thenReturn(Optional.of(UsersEntityCreator.createValidAuthenticatedUsersEntity()));

        PasswordResetTokenEntity expiredPasswordResetToken = PasswordResetTokenEntityCreator.createValidPasswordResetToken();
        expiredPasswordResetToken.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        expiredPasswordResetToken.setExpiresIn(new Date(System.currentTimeMillis() - 1000));
        // We are ensuring that the token will expire 1 second before the current moment.

        when(this.passwordResetTokenRepository.findByUsersEntityId(any())).thenReturn(Optional.of(expiredPasswordResetToken));

        CreatePasswordResetTokenDTO convertedToCreatePasswordResetTokenDTO = PasswordResetTokenMapper.toConvertCreatePasswordResetTokenDTO(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        this.passwordResetTokenService.createPasswordResetToken(convertedToCreatePasswordResetTokenDTO);

        verify(this.passwordResetTokenRepository).deleteById(expiredPasswordResetToken.getId());
    }

    @Test
    @DisplayName("User not logged should be able to reset their password")
    public void user_not_logged_should_be_able_to_reset_their_password(){
        PasswordResetTokenEntity passwordResetToken = PasswordResetTokenEntityCreator.createValidPasswordResetToken();
        passwordResetToken.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        passwordResetToken.setExpiresIn(this.genExpirationDate());

        when(this.passwordResetTokenRepository.findByToken(any())).thenReturn(Optional.of(passwordResetToken));

        when(this.usersService.getUserById(any())).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        String mockHashPassword = "$Kt$A.x5y8ZTgRp5b.Y5cX2tz4iQPj9vY0OK3h4df62NVO9fc720sZ";

        when(this.passwordEncoder.encode(any())).thenReturn(mockHashPassword);

        ResetPasswordUserNotLoggedDTO convertedResetPasswordUserNotLoggedDTO = PasswordResetTokenMapper.toConvertResetPasswordUserNotLoggedDTO(UsersEntityCreator.createUsersEntityToUpdatePassword(), passwordResetToken.getToken());

        this.passwordResetTokenService.resetPasswordUserNotLogged(convertedResetPasswordUserNotLoggedDTO);

        // Capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.passwordResetTokenRepository).deleteById(passwordResetToken.getId());

        // Verify if the method save was called with a specific argument
        verify(this.usersService).save(userCaptor.capture());

        assertEquals(mockHashPassword, userCaptor.getValue().getPassword());
    }

    @Test
    @DisplayName("User not logged should not be able to reset their password if the token is expired")
    public void user_not_logged_should_not_be_able_to_reset_their_password_if_the_token_is_expired(){
        PasswordResetTokenEntity passwordResetToken = PasswordResetTokenEntityCreator.createValidPasswordResetToken();
        passwordResetToken.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        passwordResetToken.setExpiresIn(new Date(System.currentTimeMillis() - 1000));
        // We are ensuring that the token will expire 1 second before the current moment.

        when(this.passwordResetTokenRepository.findByToken(any())).thenReturn(Optional.of(passwordResetToken));

        ResetPasswordUserNotLoggedDTO convertedResetPasswordUserNotLoggedDTO = PasswordResetTokenMapper.toConvertResetPasswordUserNotLoggedDTO(UsersEntityCreator.createUsersEntityToUpdatePassword(), passwordResetToken.getToken());

        ExpiredPasswordResetTokenException exception = assertThrows(ExpiredPasswordResetTokenException.class, () -> {
            this.passwordResetTokenService.resetPasswordUserNotLogged(convertedResetPasswordUserNotLoggedDTO);
        });

        verify(this.passwordResetTokenRepository).deleteById(passwordResetToken.getId());

        String resultMessage = "The password reset token has already expired. Please make the process again";

        assertEquals(exception.getMessage(), resultMessage);
    }

    private Date genExpirationDate() {
        long expiration = 15 * 60 * 1000;  // 15 minutes in milliseconds

        Instant now = Instant.now();

        Instant expirationTime = now.plus(expiration, ChronoUnit.MILLIS);  // // Add millis to current moment

        return Date.from(expirationTime);  // Convert Instant to date
    }
}
