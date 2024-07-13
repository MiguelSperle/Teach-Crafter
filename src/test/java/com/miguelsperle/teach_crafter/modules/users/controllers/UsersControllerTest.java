package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.modules.users.dtos.users.*;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import com.miguelsperle.teach_crafter.utils.integration.JSONConverter;
import com.miguelsperle.teach_crafter.utils.TokenGenerator;
import com.miguelsperle.teach_crafter.utils.integration.UsersUtils;
import com.miguelsperle.teach_crafter.utils.integration.configuration.H2CleanUpAndFakerExtension;
import com.miguelsperle.teach_crafter.utils.integration.configuration.interfaces.IntegrationTestSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

@IntegrationTestSetup
public class UsersControllerTest {
    @Value("${api.security.token.secret}")
    private String secret;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Should be able to register a new user")
    public void should_be_able_to_register_an_new_user() throws Exception {
        CreateUserDTO newUser = new CreateUserDTO(H2CleanUpAndFakerExtension.getFaker().name().username(), "ROLE_USER", H2CleanUpAndFakerExtension.getFaker().name().name(), H2CleanUpAndFakerExtension.getFaker().internet().emailAddress(), H2CleanUpAndFakerExtension.getFaker().internet().password());

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(newUser)));

        String expectedMessage = "Account created successfully";

        result.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.CREATED.value()));
    }

    @Test
    @DisplayName("Should not be able to register a new user if username is missing")
    public void should_not_be_able_to_register_an_new_user_if_username_is_missing() throws Exception {
        CreateUserDTO newUser = new CreateUserDTO("", "ROLE_USER", H2CleanUpAndFakerExtension.getFaker().name().name(), H2CleanUpAndFakerExtension.getFaker().internet().emailAddress(), H2CleanUpAndFakerExtension.getFaker().internet().password());

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(newUser)));

        String expectedMessage = "The field [username] is required and must not contain space";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to register a new user if role is missing")
    public void should_not_be_able_to_register_an_new_user_if_role_is_missing() throws Exception {
        CreateUserDTO newUser = new CreateUserDTO(H2CleanUpAndFakerExtension.getFaker().name().username(), "", H2CleanUpAndFakerExtension.getFaker().name().name(), H2CleanUpAndFakerExtension.getFaker().internet().emailAddress(), H2CleanUpAndFakerExtension.getFaker().internet().password());

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(newUser)));

        String expectedMessage = "Role is required to create an account";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to register a new user if name is missing")
    public void should_not_be_able_to_register_an_new_user_if_name_is_missing() throws Exception {
        CreateUserDTO newUser = new CreateUserDTO(H2CleanUpAndFakerExtension.getFaker().name().username(), "ROLE_USER", "", H2CleanUpAndFakerExtension.getFaker().internet().emailAddress(), H2CleanUpAndFakerExtension.getFaker().internet().password());

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(newUser)));

        String expectedMessage = "Name is required to create an account";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to register a new user if email is missing")
    public void should_not_be_able_to_register_an_new_user_if_email_is_missing() throws Exception {
        CreateUserDTO newUser = new CreateUserDTO(H2CleanUpAndFakerExtension.getFaker().name().username(), "ROLE_USER", H2CleanUpAndFakerExtension.getFaker().name().name(), "", H2CleanUpAndFakerExtension.getFaker().internet().password());

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(newUser)));

        String expectedMessage = "Email is required to create an account";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to register a new user if email is invalid")
    public void should_not_be_able_to_register_an_new_user_if_email_is_invalid() throws Exception {
        CreateUserDTO newUser = new CreateUserDTO(H2CleanUpAndFakerExtension.getFaker().name().username(), "ROLE_USER", H2CleanUpAndFakerExtension.getFaker().name().name(), "twqqwdqdwq", H2CleanUpAndFakerExtension.getFaker().internet().password());

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(newUser)));

        String expectedMessage = "The field [email] must contain a valid email";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to register a new user if password is invalid")
    public void should_not_be_able_to_register_an_new_user_if_password_is_invalid() throws Exception {
        CreateUserDTO newUser = new CreateUserDTO(H2CleanUpAndFakerExtension.getFaker().name().username(), "ROLE_USER", H2CleanUpAndFakerExtension.getFaker().name().name(), H2CleanUpAndFakerExtension.getFaker().internet().emailAddress(), "");

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(newUser)));

        String expectedMessage = "Password must has between 5 and 100 character";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }


    @Test
    @DisplayName("Should be able to update user name")
    public void should_be_able_to_update_user_name() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        UpdateUserNameDTO updateUserNameDTO = new UpdateUserNameDTO(H2CleanUpAndFakerExtension.getFaker().name().name());

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateUserNameDTO))
                .header("Authorization", token));

        String expectedMessage = "Name updated successfully";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to update user name if new name is missing")
    public void should_not_be_able_to_update_user_name_if_new_name_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateUserNameDTO updateUserNameDTO = new UpdateUserNameDTO("");

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateUserNameDTO))
                .header("Authorization", token));

        String expectedMessage = "A new name is required to update your current name";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should be able to update user username")
    public void should_be_able_to_update_user_username() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateUserUsernameDTO updateUserUsernameDTO = new UpdateUserUsernameDTO(H2CleanUpAndFakerExtension.getFaker().name().username(), password);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateUserUsernameDTO))
                .header("Authorization", token));

        String expectedMessage = "Username updated successfully";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to update user username if new username is missing")
    public void should_not_be_able_to_update_user_username_if_new_username_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateUserUsernameDTO updateUserUsernameDTO = new UpdateUserUsernameDTO("", password);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateUserUsernameDTO))
                .header("Authorization", token));

        String expectedMessage = "The field [newUsername] is required and must not contain space";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to update user username if current password is missing")
    public void should_not_be_able_to_update_user_username_if_current_password_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateUserUsernameDTO updateUserUsernameDTO = new UpdateUserUsernameDTO(H2CleanUpAndFakerExtension.getFaker().name().username(), "");

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateUserUsernameDTO))
                .header("Authorization", token));

        String expectedMessage = "Your current password is required to update your username";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }


    @Test
    @DisplayName("Should be able to update user email")
    public void should_be_able_to_update_user_email() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateUserEmailDTO updateUserEmailDTO = new UpdateUserEmailDTO(H2CleanUpAndFakerExtension.getFaker().internet().emailAddress(), password);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateUserEmailDTO))
                .header("Authorization", token));

        String expectedMessage = "Email updated successfully";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to update user email if new email is missing")
    public void should_not_be_able_to_update_user_email_if_new_email_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateUserEmailDTO updateUserEmailDTO = new UpdateUserEmailDTO("", password);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateUserEmailDTO))
                .header("Authorization", token));

        String expectedMessage = "A new email is required to update your current email";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to update user email if new email is invalid")
    public void should_not_be_able_to_update_user_email_if_new_email_is_invalid() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateUserEmailDTO updateUserEmailDTO = new UpdateUserEmailDTO("fwqwqfqfq", password);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateUserEmailDTO))
                .header("Authorization", token));

        String expectedMessage = "The field [newEmail] must contain a valid email";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to update user email if current password is missing")
    public void should_not_be_able_to_update_user_email_if_current_password_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateUserEmailDTO updateUserEmailDTO = new UpdateUserEmailDTO(H2CleanUpAndFakerExtension.getFaker().internet().emailAddress(), "");

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateUserEmailDTO))
                .header("Authorization", token));

        String expectedMessage = "Your current password is required to update your email";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should be able to update loggedUser password")
    public void should_be_able_to_update_loggedUser_password() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateLoggedUserPasswordDTO updateLoggedUserPasswordDTO = new UpdateLoggedUserPasswordDTO(H2CleanUpAndFakerExtension.getFaker().internet().password(), password);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateLoggedUserPasswordDTO))
                .header("Authorization", token));

        String expectedMessage = "Password updated successfully";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to update loggedUser password if new password is invalid")
    public void should_not_be_able_to_update_loggedUser_password_if_new_password_is_invalid() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateLoggedUserPasswordDTO updateLoggedUserPasswordDTO = new UpdateLoggedUserPasswordDTO("", password);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateLoggedUserPasswordDTO))
                .header("Authorization", token));

        String expectedMessage = "Password must has between 5 and 100 character";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to update loggedUser password if current password is missing")
    public void should_not_be_able_to_update_loggedUser_password_if_current_password_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateLoggedUserPasswordDTO updateLoggedUserPasswordDTO = new UpdateLoggedUserPasswordDTO(H2CleanUpAndFakerExtension.getFaker().internet().password(), "");

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateLoggedUserPasswordDTO))
                .header("Authorization", token));

        String expectedMessage = "Your current password is required to update your password";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should be able to update user image")
    public void should_be_able_to_update_user_image() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        Path imagePath = Paths.get("src/test/java/com/miguelsperle/teach_crafter/utils/integration/resources/test_images/batman.jpg");

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "batman.jpg", MediaType.IMAGE_JPEG_VALUE, Files.readAllBytes(imagePath));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/users/update-image")
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", token));

        String expectedMessage = "Image updated successfully";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to update user image if image is missing")
    public void should_not_be_able_to_update_user_image_if_image_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/users/update-image")
                .header("Authorization", token));

        String expectedMessage = "Image file is required";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should be able to fetch authenticated user profile")
    public void should_be_able_to_fetch_authenticated_user_profile() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.get("/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(userSaved.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(userSaved.getRole()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userSaved.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userSaved.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.avatar").value(userSaved.getAvatarUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").value(userSaved.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }
}
