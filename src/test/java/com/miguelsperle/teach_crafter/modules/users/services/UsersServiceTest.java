package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.dtos.users.*;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserAlreadyExistsException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserPasswordMismatchException;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import com.miguelsperle.teach_crafter.utils.unit.mocks.UsersEntityCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {
    @InjectMocks
    private UsersService usersService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CloudinaryImageService cloudinaryImageService;

    @Test
    @DisplayName("Should be able to create an user account")
    public void should_be_able_to_create_an_user_account() {
        when(this.usersRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.empty());

        UsersEntity user = UsersEntityCreator.createUsersEntityToBeSaved();

        when(this.usersRepository.save(any(UsersEntity.class))).thenReturn(user);

        CreateUserDTO createUserDTO = new CreateUserDTO(user.getUsername(), user.getRole(), user.getName(), user.getEmail(), user.getPassword());

        UsersEntity newUser = this.usersService.createUser(createUserDTO);

        assertNotNull(newUser.getId());
        assertThat(newUser).hasFieldOrProperty("id");
    }

    @Test
    @DisplayName("Should not be able to create an user account if one already exists")
    public void should_not_be_able_to_create_an_account_if_one_already_exists() {
        when(this.usersRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.of(UsersEntityCreator.createValidAuthenticatedUsersEntity()));

        UsersEntity user = UsersEntityCreator.createUsersEntityToBeSaved();

        CreateUserDTO createUserDTO = new CreateUserDTO(user.getUsername(), user.getRole(), user.getName(), user.getEmail(), user.getPassword());

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            this.usersService.createUser(createUserDTO);
        });

        String expectedErrorMessage = "User already exists";

        assertInstanceOf(UserAlreadyExistsException.class, exception);
        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    private Authentication configureAuthenticationSecurityForTest() {
        Authentication authentication = mock(Authentication.class);

        SecurityContext securityContext = mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);

        when(authentication.isAuthenticated()).thenReturn(true);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        return authentication;
    }

    @Test
    public void should_be_able_to_return_authenticated_user_profile() {
        Authentication authentication = this.configureAuthenticationSecurityForTest();

        when(authentication.getPrincipal()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        UsersEntity account = UsersEntityCreator.createValidAuthenticatedUsersEntity();

        UsersEntity authenticatedUser = this.usersService.getAuthenticatedUser();

        assertNotNull(authenticatedUser);
        assertEquals(account.getId(), authenticatedUser.getId());
        assertEquals(account.getUsername(), authenticatedUser.getUsername());
        assertEquals(account.getRole(), authenticatedUser.getRole());
        assertEquals(account.getName(), authenticatedUser.getName());
        assertEquals(account.getEmail(), authenticatedUser.getEmail());
        assertEquals(account.getPassword(), authenticatedUser.getPassword());
        assertEquals(account.getAvatarUrl(), authenticatedUser.getAvatarUrl());
        assertEquals(account.getCreatedAt(), authenticatedUser.getCreatedAt());
    }

    @Test
    @DisplayName("User should be able to update their name")
    public void user_should_be_able_to_update_their_name() {
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        UpdateUserNameDTO updateUserNameDTO = new UpdateUserNameDTO(UsersEntityCreator.createUsersEntityToUpdateName().getName());

        this.usersService.updateUserName(updateUserNameDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.usersRepository).save(userCaptor.capture());

        assertEquals(updateUserNameDTO.newName(), userCaptor.getValue().getName());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("User should be able to update their username")
    public void user_should_be_able_to_update_their_username() {
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.usersRepository.findByUsername(any())).thenReturn(Optional.empty());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(true);

        UpdateUserUsernameDTO updateUserUsernameDTO = new UpdateUserUsernameDTO(UsersEntityCreator.createUsersEntityToUpdateUsername().getUsername(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        this.usersService.updateUserUsername(updateUserUsernameDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.usersRepository).save(userCaptor.capture());

        assertEquals(updateUserUsernameDTO.newUsername(), userCaptor.getValue().getUsername());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("User should not be able to update their username if another user already has the same username")
    public void user_should_not_be_able_to_update_their_username_if_another_user_already_has_the_same_username() {
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.usersRepository.findByUsername(any())).thenReturn(Optional.of(UsersEntityCreator.createSecondValidUsersEntity()));

        UpdateUserUsernameDTO updateUserUsernameDTO = new UpdateUserUsernameDTO(UsersEntityCreator.createUsersEntityToUpdateUsername().getUsername(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            this.usersService.updateUserUsername(updateUserUsernameDTO);
        });

        String expectedErrorMessage = "This username is already used";

        assertInstanceOf(UserAlreadyExistsException.class, exception);
        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("User should not be able to update their username with an invalid password")
    public void user_should_not_be_able_to_update_their_username_with_an_invalid_password() {
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.usersRepository.findByUsername(any())).thenReturn(Optional.empty());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(false);

        UpdateUserUsernameDTO updateUserUsernameDTO = new UpdateUserUsernameDTO(UsersEntityCreator.createUsersEntityToUpdateUsername().getUsername(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        UserPasswordMismatchException exception = assertThrows(UserPasswordMismatchException.class, () -> {
            this.usersService.updateUserUsername(updateUserUsernameDTO);
        });

        String expectedErrorMessage = "Incorrect current password";

        assertInstanceOf(UserPasswordMismatchException.class, exception);
        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("User should be able to update their email")
    public void user_should_be_able_to_update_their_email() {
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.usersRepository.findByEmail(any())).thenReturn(Optional.empty());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(true);

        UpdateUserEmailDTO  updateUserEmailDTO = new UpdateUserEmailDTO(UsersEntityCreator.createUsersEntityToUpdateEmail().getEmail(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        this.usersService.updateUserEmail(updateUserEmailDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.usersRepository).save(userCaptor.capture());

        assertEquals(updateUserEmailDTO.newEmail(), userCaptor.getValue().getEmail());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("User should not be able to update their email if another user already has the same email")
    public void user_should_not_be_able_to_update_their_email_if_another_user_already_has_the_same_email() {
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.usersRepository.findByEmail(any())).thenReturn(Optional.of(UsersEntityCreator.createSecondValidUsersEntity()));

        UpdateUserEmailDTO  updateUserEmailDTO = new UpdateUserEmailDTO(UsersEntityCreator.createUsersEntityToUpdateEmail().getEmail(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            this.usersService.updateUserEmail(updateUserEmailDTO);
        });

        String expectedErrorMessage = "This email is already used";

        assertInstanceOf(UserAlreadyExistsException.class, exception);
        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("User should not be able to update their email with an invalid password")
    public void user_should_not_be_able_to_update_their_email_with_an_invalid_password() {
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.usersRepository.findByEmail(any())).thenReturn(Optional.empty());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(false);

        UpdateUserEmailDTO  updateUserEmailDTO = new UpdateUserEmailDTO(UsersEntityCreator.createUsersEntityToUpdateEmail().getEmail(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        UserPasswordMismatchException exception = assertThrows(UserPasswordMismatchException.class, () -> {
            this.usersService.updateUserEmail(updateUserEmailDTO);
        });

        String expectedErrorMessage = "Incorrect current password";

        assertInstanceOf(UserPasswordMismatchException.class, exception);
        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("User should be able to update their password")
    public void user_should_be_able_to_update_their_password() {
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(true);

        String mockHashPassword = "$20$y3M.s6zqRz9eZe.2Tyy8GOo32fwWE0q2c0bFIE2O1.MJBkR5a6";

        when(this.passwordEncoder.encode(any())).thenReturn(mockHashPassword);

        UpdateLoggedUserPasswordDTO updateLoggedUserPasswordDTO = new UpdateLoggedUserPasswordDTO(UsersEntityCreator.createUsersEntityToUpdatePassword().getPassword(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        this.usersService.updateLoggedUserPassword(updateLoggedUserPasswordDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.usersRepository).save(userCaptor.capture());

        assertEquals(mockHashPassword, userCaptor.getValue().getPassword());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("User should not be able to update their password with an invalid password")
    public void user_should_not_be_able_to_update_their_password_with_an_invalid_password() {
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(false);

        UpdateLoggedUserPasswordDTO updateLoggedUserPasswordDTO = new UpdateLoggedUserPasswordDTO(UsersEntityCreator.createUsersEntityToUpdatePassword().getPassword(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        UserPasswordMismatchException exception = assertThrows(UserPasswordMismatchException.class, () -> {
            this.usersService.updateLoggedUserPassword(updateLoggedUserPasswordDTO);
        });

        String expectedErrorMessage = "Incorrect current password";

        assertInstanceOf(UserPasswordMismatchException.class, exception);
        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("User should be able to update their image")
    public void user_should_be_able_to_update_their_image() {
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getAuthenticatedUser()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        MultipartFile mockImageFile = mock(MultipartFile.class);

        String expectedUrl = "NEW_IMAGE_URL";
        when(this.cloudinaryImageService.uploadImageFile(mockImageFile, "profile_pics")).thenReturn(expectedUrl);

        this.usersService.updateUserImage(mockImageFile);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.usersRepository).save(userCaptor.capture());

        assertEquals(expectedUrl, userCaptor.getValue().getAvatarUrl());
        // First argument is what I expect
        // Second argument is the real value obtained
    }
}
