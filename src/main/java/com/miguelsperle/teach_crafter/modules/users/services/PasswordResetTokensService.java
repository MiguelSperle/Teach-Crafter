package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetTokens.CreatePasswordResetTokenDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetTokens.ResetPasswordUserNotLoggedDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.PasswordResetTokensEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.exceptions.ActivePasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.exceptions.ExpiredPasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.exceptions.PasswordResetTokenNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.repositories.PasswordResetTokensRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class PasswordResetTokensService {
    @Autowired
    private PasswordResetTokensRepository passwordResetTokenRepository;
    @Autowired
    private UsersService usersService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailSenderService emailSenderService;

    public PasswordResetTokensEntity createPasswordResetToken(CreatePasswordResetTokenDTO createPasswordResetTokenDTO){
        PasswordResetTokensEntity newPasswordResetToken = new PasswordResetTokensEntity();

        this.verifyPasswordResetTokenAlreadyExists(createPasswordResetTokenDTO.currentEmail());

        Optional<UsersEntity> user = this.usersService.getUserByEmail(createPasswordResetTokenDTO.currentEmail());

        newPasswordResetToken.setToken(this.genToken());
        newPasswordResetToken.setExpiresIn(this.genExpirationDate());
        newPasswordResetToken.setUsersEntity(user.get());

        this.sendPasswordResetTokenEmail(user.get(), this.genToken());

        return this.passwordResetTokenRepository.save(newPasswordResetToken);
    }

    private void verifyPasswordResetTokenAlreadyExists(String email) {
        UsersEntity user = this.usersService.getUserByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<PasswordResetTokensEntity> passwordResetToken = this.passwordResetTokenRepository.findByUsersEntityId(user.getId());

        if (passwordResetToken.isPresent()) {
            if (this.isPasswordResetTokenExpired(passwordResetToken.get())) {
                this.deleteExpiredPasswordResetToken(passwordResetToken.get());
            } else {
                this.sendPasswordResetTokenEmail(user, passwordResetToken.get().getToken());
                throw new ActivePasswordResetTokenException("You have an active password reset token. Please check your email to continue with password recovery.");
            }
        }
    }

    private void sendPasswordResetTokenEmail(UsersEntity user, String token) {
        this.emailSenderService.sendSimpleMessage(user.getEmail(), "Recuperação de senha", token);
    }

    private void deleteExpiredPasswordResetToken(PasswordResetTokensEntity passwordResetToken) {
        this.passwordResetTokenRepository.deleteById(passwordResetToken.getId());
    }

    private boolean isPasswordResetTokenExpired(PasswordResetTokensEntity passwordResetToken) {
        Date now = new Date();
        return now.after(passwordResetToken.getExpiresIn());
    }

    private Date genExpirationDate() {
        long expiration = 15 * 60 * 1000;  // 15 minutes in milliseconds

        Instant now = Instant.now();

        Instant expirationTime = now.plus(expiration, ChronoUnit.MILLIS);  // // Add millis to current moment

        return Date.from(expirationTime);  // Convert Instant to date
    }

    private String genToken(){
        int tokenByteLength = 24;

        byte[] randomBytes = new byte[tokenByteLength];
        new SecureRandom().nextBytes(randomBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes); // 32 characters
    }

    private PasswordResetTokensEntity getPasswordResetTokenByToken(String token){
        return this.passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new PasswordResetTokenNotFoundException("Password reset token not found"));
    }

    public void resetPasswordUserNotLogged(ResetPasswordUserNotLoggedDTO resetPasswordUserNotLoggedDTO){
        PasswordResetTokensEntity passwordResetToken = this.getPasswordResetTokenByToken(resetPasswordUserNotLoggedDTO.token());

        this.verifyExpiredPasswordResetToken(passwordResetToken);

        UsersEntity user = this.usersService.getUserById(passwordResetToken.getUsersEntity().getId());

        user.setPassword(passwordEncoder.encode(resetPasswordUserNotLoggedDTO.newPassword()));
        this.deleteExpiredPasswordResetToken(passwordResetToken);

        this.usersService.save(user);
    }

    private void verifyExpiredPasswordResetToken(PasswordResetTokensEntity passwordResetToken) {
        if (this.isPasswordResetTokenExpired(passwordResetToken)) {
            this.deleteExpiredPasswordResetToken(passwordResetToken);
            throw new ExpiredPasswordResetTokenException("The password reset token has already expired. Please make the process again");
        }
    }
}
