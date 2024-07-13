package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetTokens.CreatePasswordResetTokenDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetTokens.ResetPasswordUserNotLoggedDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.PasswordResetTokensEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.PasswordResetTokensRepository;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import com.miguelsperle.teach_crafter.utils.integration.PasswordResetTokensUtils;
import com.miguelsperle.teach_crafter.utils.integration.UsersUtils;
import com.miguelsperle.teach_crafter.utils.integration.JSONConverter;
import com.miguelsperle.teach_crafter.utils.integration.configuration.H2CleanUpAndFakerExtension;
import com.miguelsperle.teach_crafter.utils.integration.configuration.interfaces.IntegrationTestSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@IntegrationTestSetup
public class PasswordResetTokensControllerTest {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordResetTokensRepository passwordResetTokensRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Should be able to send reset password email")
    public void should_be_able_to_send_reset_password_email() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        CreatePasswordResetTokenDTO createPasswordResetTokenDTO = new CreatePasswordResetTokenDTO(userSaved.getEmail());

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/reset-password/send-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(createPasswordResetTokenDTO)));

        String expectedMessage = "Please check your email";

        result.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.CREATED.value()));
    }

    @Test
    @DisplayName("Should be able to send reset password email even if there is an active token")
    public void should_be_able_to_send_reset_password_email_even_if_there_is_an_active_token() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        PasswordResetTokensEntity passwordResetToken = PasswordResetTokensUtils.createPasswordResetToken(userSaved, this.genToken(), this.genExpirationDate());

        this.passwordResetTokensRepository.save(passwordResetToken);

        CreatePasswordResetTokenDTO createPasswordResetTokenDTO = new CreatePasswordResetTokenDTO(userSaved.getEmail());

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/reset-password/send-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(createPasswordResetTokenDTO)));

        String expectedMessage = "You have an active password reset token. Please check your email to continue with password recovery";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to send reset password email if current email is missing")
    public void should_not_be_able_to_send_reset_password_email_if_current_email_is_missing() throws Exception {
        CreatePasswordResetTokenDTO createPasswordResetTokenDTO = new CreatePasswordResetTokenDTO("");

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/reset-password/send-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(createPasswordResetTokenDTO)));

        String expectedMessage = "Your current email is required to create a token to reset your current password";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to send reset password email if current email is invalid")
    public void should_not_be_able_to_send_reset_password_email_if_current_email_is_invalid() throws Exception {
        CreatePasswordResetTokenDTO createPasswordResetTokenDTO = new CreatePasswordResetTokenDTO("fwqcqwqweqw");

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/reset-password/send-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(createPasswordResetTokenDTO)));

        String expectedMessage = "The field [currentEmail] must contain a valid email";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should be able to reset password")
    public void should_be_able_to_reset_password() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        PasswordResetTokensEntity passwordResetToken = PasswordResetTokensUtils.createPasswordResetToken(userSaved, this.genToken(), this.genExpirationDate());

        PasswordResetTokensEntity passwordResetTokenSaved = this.passwordResetTokensRepository.saveAndFlush(passwordResetToken);

        String newPassword = H2CleanUpAndFakerExtension.getFaker().internet().password();

        ResetPasswordUserNotLoggedDTO resetPasswordUserNotLoggedDTO = new ResetPasswordUserNotLoggedDTO(newPassword, passwordResetTokenSaved.getToken());

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(resetPasswordUserNotLoggedDTO)));

        String expectedMessage = "Password reset successfully";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to reset password if new password is missing")
    public void should_not_be_able_to_reset_password_if_new_password_is_missing() throws Exception {
        ResetPasswordUserNotLoggedDTO resetPasswordUserNotLoggedDTO = new ResetPasswordUserNotLoggedDTO("", this.genToken());

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(resetPasswordUserNotLoggedDTO)));

        String expectedMessage = "Password must has between 5 and 100 character";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to reset password if token is missing")
    public void should_not_be_able_to_reset_password_if_token_is_missing() throws Exception {
        String newPassword = H2CleanUpAndFakerExtension.getFaker().internet().password();

        ResetPasswordUserNotLoggedDTO resetPasswordUserNotLoggedDTO = new ResetPasswordUserNotLoggedDTO(newPassword, "");

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(resetPasswordUserNotLoggedDTO)));

        String expectedMessage = "The token is required to reset your current password";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }


    private String genToken() {
        int tokenByteLength = 24;

        byte[] randomBytes = new byte[tokenByteLength];
        new SecureRandom().nextBytes(randomBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes); // 32 characters
    }

    private Date genExpirationDate() {
        long expiration = 15 * 60 * 1000;  // 15 minutes in milliseconds

        Instant now = Instant.now();

        Instant expirationTime = now.plus(expiration, ChronoUnit.MILLIS);  // // Add millis to current moment

        return Date.from(expirationTime);  // Convert Instant to date
    }
}
