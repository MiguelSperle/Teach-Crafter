package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetToken.CreatePasswordResetTokenDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetToken.ResetPasswordUserNotLoggedDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.PasswordResetTokenEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.ActivePasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.ExpiredPasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.PasswordResetTokenNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.repositories.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UsersService usersService;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;

    public void createPasswordResetToken(CreatePasswordResetTokenDTO createPasswordResetTokenDTO){
        PasswordResetTokenEntity newPasswordResetToken = new PasswordResetTokenEntity();

        this.verificationAlreadyExistsPasswordResetToken(createPasswordResetTokenDTO.currentEmail());

        Optional<UsersEntity> user = this.usersService.getUserByEmail(createPasswordResetTokenDTO.currentEmail());

        newPasswordResetToken.setToken(this.genToken());
        newPasswordResetToken.setExpiresIn(this.genExpirationDate());
        newPasswordResetToken.setUsersEntity(user.get());

        this.emailSenderService.sendSimpleMessage(createPasswordResetTokenDTO.currentEmail(), "Recuperação de senha", this.genToken());

        this.passwordResetTokenRepository.save(newPasswordResetToken);
    }

    private void verificationAlreadyExistsPasswordResetToken(String email) {
        UsersEntity user = this.usersService.getUserByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<PasswordResetTokenEntity> passwordResetToken = this.passwordResetTokenRepository.findByUsersEntityId(user.getId());

        if (passwordResetToken.isPresent()) {
            if (this.isPasswordResetTokenExpired(passwordResetToken.get())) {
                this.deleteExpiredPasswordResetToken(passwordResetToken.get());
            } else {
                this.sendPasswordResetTokenEmail(user, passwordResetToken.get());
                throw new ActivePasswordResetTokenException("You have an active password reset token. Please check your email to continue with password recovery.");
            }
        }
    }

    private void sendPasswordResetTokenEmail(UsersEntity user, PasswordResetTokenEntity passwordResetToken) {
        this.emailSenderService.sendSimpleMessage(user.getEmail(), "Recuperação de senha", passwordResetToken.getToken());
    }

    private void deleteExpiredPasswordResetToken(PasswordResetTokenEntity passwordResetToken) {
        this.passwordResetTokenRepository.deleteById(passwordResetToken.getId());
    }

    private boolean isPasswordResetTokenExpired(PasswordResetTokenEntity passwordResetToken) {
        Date now = new Date();
        return now.after(passwordResetToken.getExpiresIn());
    }

    private Date genExpirationDate() {
        long expiration = 2 * 60 * 1000;  // 15 minutes in milliseconds

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

    private PasswordResetTokenEntity getPasswordResetTokenByToken(String token){
        return this.passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new PasswordResetTokenNotFoundException("Password reset token not found"));
    }

    public void resetPasswordUserNotLogged(ResetPasswordUserNotLoggedDTO resetPasswordUserNotLoggedDTO){
        PasswordResetTokenEntity passwordResetToken = this.getPasswordResetTokenByToken(resetPasswordUserNotLoggedDTO.token());

        this.verificationExpiredPasswordResetToken(passwordResetToken);

        UsersEntity user = this.usersService.getUserById(passwordResetToken.getUsersEntity().getId());

        user.setPassword(passwordEncoder.encode(resetPasswordUserNotLoggedDTO.newPassword()));
        this.deleteExpiredPasswordResetToken(passwordResetToken);

        this.usersService.save(user);
    }

    private void verificationExpiredPasswordResetToken(PasswordResetTokenEntity passwordResetToken) {
        if (this.isPasswordResetTokenExpired(passwordResetToken)) {
            this.deleteExpiredPasswordResetToken(passwordResetToken);
            throw new ExpiredPasswordResetTokenException("The password reset token has already expired. Please make the process again");
        }
    }
}
