package com.miguelsperle.teach_crafter;

import com.miguelsperle.teach_crafter.modules.users.dtos.users.*;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
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
        when(this.usersRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.of(UsersEntityCreator.createValidUsersEntity()));

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
    @DisplayName("Should be able to update the name of a logged user")
    public void should_be_able_to_update_the_name_of_a_logged_user(){
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidUsersEntity());

        UpdateNameUserDTO convertedToUpdateNameUserDTO = UsersMapper.toConvertUpdateNameUserDTO(UsersEntityCreator.createUsersEntityToUpdateName());

        this.usersService.updateNameUser(convertedToUpdateNameUserDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.usersRepository).save(userCaptor.capture());

        assertThat(convertedToUpdateNameUserDTO.newName()).isEqualTo(userCaptor.getValue().getName());
    }

    @Test
    @DisplayName("Should be able to update the username of a logged user")
    public void should_be_able_to_update_the_username_of_a_logged_user(){
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidUsersEntity());

        when(this.usersRepository.findByUsername(any())).thenReturn(Optional.empty());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(true);

        UpdateUsernameUserDTO convertedToUpdateUsernameUserDTO = UsersMapper.toConvertUpdateUsernameUserDTO(UsersEntityCreator.createUsersEntityToUpdateUsername());

        this.usersService.updateUsernameUser(convertedToUpdateUsernameUserDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.usersRepository).save(userCaptor.capture());

        assertThat(convertedToUpdateUsernameUserDTO.newUsername()).isEqualTo(userCaptor.getValue().getUsername());
    }

    @Test
    @DisplayName("Should be able to update the email of a logged user")
    public void should_be_able_to_update_the_email_of_a_logged_user(){
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidUsersEntity());

        when(this.usersRepository.findByEmail(any())).thenReturn(Optional.empty());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(true);

        UpdateEmailUserDTO convertedToUpdateEmailUserDTO = UsersMapper.toConvertUpdateEmailUserDTO(UsersEntityCreator.createUsersEntityToUpdateEmail());

        this.usersService.updateEmailUser(convertedToUpdateEmailUserDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.usersRepository).save(userCaptor.capture());

        assertThat(convertedToUpdateEmailUserDTO.newEmail()).isEqualTo(userCaptor.getValue().getEmail());
    }

    @Test
    @DisplayName("Should be able to update the password of a logged user")
    public void should_be_able_to_update_the_password_of_a_logged_user(){
        this.configureAuthenticationSecurityForTest();

        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidUsersEntity());

        when(this.passwordEncoder.matches(any(), any())).thenReturn(true);

        String mockHashPassword = "$20$y3M.s6zqRz9eZe.2Tyy8GOo32fwWE0q2c0bFIE2O1.MJBkR5a6";

        when(this.passwordEncoder.encode(any())).thenReturn(mockHashPassword);

        UpdatePasswordUserLoggedDTO convertedToUpdatePasswordUserLoggedDTO = UsersMapper.toConvertUpdatePasswordUserLoggedDTO(UsersEntityCreator.createUsersEntityToUpdatePassword());

        this.usersService.updatePasswordUserLogged(convertedToUpdatePasswordUserLoggedDTO);

        // capture the value after of the method called ( save() )
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        // Verify if the method save was called with a specific argument
        verify(this.usersRepository).save(userCaptor.capture());

        assertThat(mockHashPassword).isEqualTo(userCaptor.getValue().getPassword());
    }
}
