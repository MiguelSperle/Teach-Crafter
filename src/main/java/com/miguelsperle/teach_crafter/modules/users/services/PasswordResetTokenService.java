package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetToken.CreatePasswordResetTokenDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.users.UpdatePasswordUserNotLoggedDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.PasswordResetTokenEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.MakeTheProcessAgainException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.PasswordResetTokenNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.PasswordResetTokenRecoveryIsNotExpiredException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
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

    private void verificationAlreadyExistsPasswordResetToken(String email){
        UsersEntity user = this.usersService.getUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Optional<PasswordResetTokenEntity> passwordResetToken = this.passwordResetTokenRepository.findByUsersEntityId(user.getId());

         if(passwordResetToken.isPresent()){
             Date now = new Date();

             boolean passwordResetTokenExpired = now.after(passwordResetToken.get().getExpiresIn());

             if(passwordResetTokenExpired){
                 this.passwordResetTokenRepository.deleteById(passwordResetToken.get().getId());
             } else {
                 this.emailSenderService.sendSimpleMessage(user.getEmail(), "Recuperação de senha", passwordResetToken.get().getToken());
                 throw new PasswordResetTokenRecoveryIsNotExpiredException("The token still is not expired");
             }
         }
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

    public PasswordResetTokenEntity getPasswordResetTokenByToken(String token){
        return this.passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new PasswordResetTokenNotFoundException("Password reset token not found"));
    }

    public void updatePasswordUserNotLogged(UpdatePasswordUserNotLoggedDTO updatePasswordUserNotLoggedDTO){
        PasswordResetTokenEntity passwordResetToken = this.getPasswordResetTokenByToken(updatePasswordUserNotLoggedDTO.token());

        this.verificationPasswordResetTokenExpired(passwordResetToken.getId(), passwordResetToken.getExpiresIn());

        UsersEntity user = this.usersService.getUserById(passwordResetToken.getUsersEntity().getId());

        user.setPassword(passwordEncoder.encode(updatePasswordUserNotLoggedDTO.newPassword()));
        this.passwordResetTokenRepository.deleteById(passwordResetToken.getId());

        this.usersService.save(user);
    }

    private void verificationPasswordResetTokenExpired(String id, Date expiresIn){
        Date now = new Date();

        boolean passwordResetTokenExpired = now.after(expiresIn);

        if(passwordResetTokenExpired){
            this.passwordResetTokenRepository.deleteById(id);
            throw new MakeTheProcessAgainException("Make the process again");
        }
    }
}
