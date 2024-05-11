package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.dtos.cloudinary.UploadImageModelDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.users.*;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.PasswordNotMatchUserException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserAlreadyExistsException;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import com.miguelsperle.teach_crafter.modules.users.services.CloudinaryService;
import com.miguelsperle.teach_crafter.modules.users.services.UsersService;
import com.miguelsperle.teach_crafter.utils.mappers.UsersMapper;
import com.miguelsperle.teach_crafter.utils.mocks.UsersEntityCreator;
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
    private CloudinaryService cloudinaryService;

    @Test
    @DisplayName("Should be able to create an user account")
    public void should_be_able_to_create_an_user_account() {
        when(this.usersRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.empty());

        when(this.usersRepository.save(any(UsersEntity.class))).thenReturn(UsersEntityCreator.createUsersEntityToBeSaved());

        CreateUserDTO convertedToCreateUserDTO = UsersMapper.toConvertCreateUserDTO(UsersEntityCreator.createUsersEntityToBeSaved());

        UsersEntity newUser = this.usersService.createUser(convertedToCreateUserDTO);

        assertNotNull(newUser.getId());
        assertThat(newUser).hasFieldOrProperty("id");
    }

    @Test
    @DisplayName("Should not be able to create an user account if one already exists")
    public void should_not_be_able_to_create_an_account_if_one_already_exists() {
        when(this.usersRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.of(UsersEntityCreator.createValidAuthenticatedUsersEntity()));

        CreateUserDTO convertedToCreateUserDTO = UsersMapper.toConvertCreateUserDTO(UsersEntityCreator.createUsersEntityToBeSaved());

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            this.usersService.createUser(convertedToCreateUserDTO);
        });

        assertThat(exception.getMessage()).isEqualTo("User already exists");
    }

    private void configureAuthenticationSecurityForTest(){
        Authentication authentication = mock(Authentication.class);

        SecurityContext securityContext = mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);

        when(authentication.isAuthenticated()).thenReturn(true);

        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    @DisplayName("User should be able to update their name")
    public void user_should_be_able_to_update_their_name(){
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        UpdateNameUserDTO convertedToUpdateNameUserDTO = UsersMapper.toConvertUpdateNameUserDTO(UsersEntityCreator.createUsersEntityToUpdateName());

        this.usersService.updateNameUser(convertedToUpdateNameUserDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.usersRepository).save(userCaptor.capture());

        assertEquals(convertedToUpdateNameUserDTO.newName(), userCaptor.getValue().getName());
    }

    @Test
    @DisplayName("User should be able to update their username")
    public void user_should_be_able_to_update_their_username(){
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.usersRepository.findByUsername(any())).thenReturn(Optional.empty());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(true);

        UpdateUsernameUserDTO convertedToUpdateUsernameUserDTO = UsersMapper.toConvertUpdateUsernameUserDTO(UsersEntityCreator.createUsersEntityToUpdateUsername(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        this.usersService.updateUsernameUser(convertedToUpdateUsernameUserDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.usersRepository).save(userCaptor.capture());

        assertEquals(convertedToUpdateUsernameUserDTO.newUsername(), userCaptor.getValue().getUsername());
    }

    @Test
    @DisplayName("User should not be able to update their username if another user already has the same username")
    public void user_should_not_be_able_to_update_their_username_if_another_user_already_has_the_same_username(){
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.usersRepository.findByUsername(any())).thenReturn(Optional.of(UsersEntityCreator.createSecondValidUsersEntity()));

        UpdateUsernameUserDTO convertedToUpdateUsernameUserDTO = UsersMapper.toConvertUpdateUsernameUserDTO(UsersEntityCreator.createUsersEntityToUpdateUsername(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            this.usersService.updateUsernameUser(convertedToUpdateUsernameUserDTO);
        });

        assertThat(exception.getMessage()).isEqualTo("This username is already used");
    }

    @Test
    @DisplayName("User should not be able to update their username with an invalid password")
    public void user_should_not_be_able_to_update_their_username_with_an_invalid_password(){
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.usersRepository.findByUsername(any())).thenReturn(Optional.empty());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(false);

        UpdateUsernameUserDTO convertedToUpdateUsernameUserDTO = UsersMapper.toConvertUpdateUsernameUserDTO(UsersEntityCreator.createUsersEntityToUpdateUsername(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        PasswordNotMatchUserException exception = assertThrows(PasswordNotMatchUserException.class, () -> {
            this.usersService.updateUsernameUser(convertedToUpdateUsernameUserDTO);
        });

        assertThat(exception.getMessage()).isEqualTo("Incorrect password");
    }

    @Test
    @DisplayName("User should be able to update their email")
    public void user_should_be_able_to_update_their_email(){
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.usersRepository.findByEmail(any())).thenReturn(Optional.empty());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(true);

        UpdateEmailUserDTO convertedToUpdateEmailUserDTO = UsersMapper.toConvertUpdateEmailUserDTO(UsersEntityCreator.createUsersEntityToUpdateEmail(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        this.usersService.updateEmailUser(convertedToUpdateEmailUserDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.usersRepository).save(userCaptor.capture());

        assertEquals(convertedToUpdateEmailUserDTO.newEmail(), userCaptor.getValue().getEmail());
    }

    @Test
    @DisplayName("User should not be able to update their email if another user already has the same email")
    public void user_should_not_be_able_to_update_their_email_if_another_user_already_has_the_same_email(){
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.usersRepository.findByEmail(any())).thenReturn(Optional.of(UsersEntityCreator.createSecondValidUsersEntity()));

        UpdateEmailUserDTO convertedToUpdateEmailUserDTO = UsersMapper.toConvertUpdateEmailUserDTO(UsersEntityCreator.createUsersEntityToUpdateEmail(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            this.usersService.updateEmailUser(convertedToUpdateEmailUserDTO);
        });

        assertThat(exception.getMessage()).isEqualTo("This email is already used");
    }

    @Test
    @DisplayName("User should not be able to update their email with an invalid password")
    public void user_should_not_be_able_to_update_their_email_with_an_invalid_password(){
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.usersRepository.findByEmail(any())).thenReturn(Optional.empty());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(false);

        UpdateEmailUserDTO convertedToUpdateEmailUserDTO = UsersMapper.toConvertUpdateEmailUserDTO(UsersEntityCreator.createUsersEntityToUpdateEmail(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        PasswordNotMatchUserException exception = assertThrows(PasswordNotMatchUserException.class, () -> {
            this.usersService.updateEmailUser(convertedToUpdateEmailUserDTO);
        });

        assertThat(exception.getMessage()).isEqualTo("Incorrect password");
    }

    @Test
    @DisplayName("User should be able to update their password")
    public void user_should_be_able_to_update_their_password(){
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(true);

        String mockHashPassword = "$20$y3M.s6zqRz9eZe.2Tyy8GOo32fwWE0q2c0bFIE2O1.MJBkR5a6";

        when(this.passwordEncoder.encode(any())).thenReturn(mockHashPassword);

        UpdatePasswordUserLoggedDTO convertedToUpdatePasswordUserLoggedDTO = UsersMapper.toConvertUpdatePasswordUserLoggedDTO(UsersEntityCreator.createUsersEntityToUpdatePassword(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        this.usersService.updatePasswordUserLogged(convertedToUpdatePasswordUserLoggedDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.usersRepository).save(userCaptor.capture());

        assertEquals(mockHashPassword, userCaptor.getValue().getPassword());
    }

    @Test
    @DisplayName("User should not be able to update their password with an invalid password")
    public void user_should_not_be_able_to_update_their_password_with_an_invalid_password(){
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(false);

        UpdatePasswordUserLoggedDTO convertedToUpdatePasswordUserLoggedDTO = UsersMapper.toConvertUpdatePasswordUserLoggedDTO(UsersEntityCreator.createUsersEntityToUpdatePassword(), UsersEntityCreator.createValidCurrentPasswordAuthenticatedUsersEntity().getPassword());

        PasswordNotMatchUserException exception = assertThrows(PasswordNotMatchUserException.class, () -> {
            this.usersService.updatePasswordUserLogged(convertedToUpdatePasswordUserLoggedDTO);
        });

        assertThat(exception.getMessage()).isEqualTo("Incorrect password");
    }

    @Test
    @DisplayName("User should be able to update their image")
    public void user_should_be_able_to_update_their_image(){
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        MultipartFile mockFile = mock(MultipartFile.class);
        UploadImageModelDTO uploadImageModelDTO = new UploadImageModelDTO(mockFile);

        String expectedUrl = "new_profile_picture_url_when_the_user_updates_their_profile_picture";
        when(this.cloudinaryService.uploadFile(mockFile, "profile_pics")).thenReturn(expectedUrl);

        this.usersService.updateImageUser(uploadImageModelDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.usersRepository).save(userCaptor.capture());

        System.out.println(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        System.out.println(userCaptor.getValue());

        assertEquals(expectedUrl, userCaptor.getValue().getAvatar());
    }
}