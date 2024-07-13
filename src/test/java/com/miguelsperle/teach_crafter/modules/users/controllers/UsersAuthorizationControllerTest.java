package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.modules.users.dtos.authorization.UsersAuthorizationDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import com.miguelsperle.teach_crafter.utils.integration.JSONConverter;
import com.miguelsperle.teach_crafter.utils.integration.UsersUtils;
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

@IntegrationTestSetup
public class UsersAuthorizationControllerTest {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Should be able to login")
    public void should_be_able_to_login() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);
        
        UsersAuthorizationDTO usersAuthorizationDTO = new UsersAuthorizationDTO(userSaved.getEmail(), password);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(usersAuthorizationDTO)));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to login if email is missing")
    public void should_not_be_able_to_login_if_email_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersAuthorizationDTO usersAuthorizationDTO = new UsersAuthorizationDTO("", password);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(usersAuthorizationDTO)));

        String expectedMessage = "Email is required to login";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to login if email is invalid")
    public void should_not_be_able_to_login_if_email_is_invalid() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersAuthorizationDTO usersAuthorizationDTO = new UsersAuthorizationDTO("rwqrqrqc", password);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(usersAuthorizationDTO)));

        String expectedMessage = "The field [email] must contain a valid email";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to login if password is missing")
    public void should_not_be_able_to_login_if_password_is_missing() throws Exception {
        String email = H2CleanUpAndFakerExtension.getFaker().internet().emailAddress();

        UsersAuthorizationDTO usersAuthorizationDTO = new UsersAuthorizationDTO(email, "");

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(usersAuthorizationDTO)));

        String expectedMessage = "Password is required to login";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }
}
