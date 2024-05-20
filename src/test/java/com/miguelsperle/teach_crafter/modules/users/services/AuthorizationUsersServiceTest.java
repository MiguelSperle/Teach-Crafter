package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.infra.security.TokenService;
import com.miguelsperle.teach_crafter.modules.users.dtos.authorization.AuthorizationUsersDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserPasswordMismatchException;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import com.miguelsperle.teach_crafter.utils.mappers.UsersMapper;
import com.miguelsperle.teach_crafter.utils.mocks.TokenGenerator;
import com.miguelsperle.teach_crafter.utils.mocks.UsersEntityCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorizationUsersServiceTest {
    @InjectMocks
    private AuthorizationUsersService authorizationUsersService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    private void configureValidAuthenticatedUserMockForTest(){
        when(this.usersRepository.findByEmail(any())).thenReturn(Optional.of(UsersEntityCreator.createValidAuthenticatedUsersEntity()));
    }

    @Test
    @DisplayName("Should be able to login with valid account credentials")
    public void should_be_able_to_login_with_valid_account_credentials(){
        this.configureValidAuthenticatedUserMockForTest();

        when(this.passwordEncoder.matches(any(), any())).thenReturn(true);

        String generatedToken = TokenGenerator.generateToken(UsersEntityCreator.createValidAuthenticatedUsersEntity().getId(), "secret_key_authorization_test");

        when(this.tokenService.generateToken(any())).thenReturn(generatedToken);

        AuthorizationUsersDTO convertedToAuthorizationUsersDTO = UsersMapper.toConvertAuthorizationUsersDTO(UsersEntityCreator.createUsersEntityToLogin());

        String token = this.authorizationUsersService.authorizationUsers(convertedToAuthorizationUsersDTO);

        assertNotNull(token);
        assertEquals(generatedToken, token);
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("Should not be able to login with an invalid email")
    public void should_not_be_able_to_login_with_an_invalid_email(){
        when(this.usersRepository.findByEmail(any())).thenReturn(Optional.empty());

        AuthorizationUsersDTO convertedToAuthorizationUsersDTO = UsersMapper.toConvertAuthorizationUsersDTO(UsersEntityCreator.createUsersEntityToLogin());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            this.authorizationUsersService.authorizationUsers(convertedToAuthorizationUsersDTO);
        });

        String expectedErrorMessage = "Email/password incorrect";

        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("Should not be able to login with an invalid password")
    public void should_not_be_able_to_login_with_an_invalid_password(){
        this.configureValidAuthenticatedUserMockForTest();

        when(this.passwordEncoder.matches(any(), any())).thenReturn(false);

        AuthorizationUsersDTO convertedToAuthorizationUsersDTO = UsersMapper.toConvertAuthorizationUsersDTO(UsersEntityCreator.createUsersEntityToLogin());

        UserPasswordMismatchException exception = assertThrows(UserPasswordMismatchException.class, () -> {
            this.authorizationUsersService.authorizationUsers(convertedToAuthorizationUsersDTO);
        });

        String expectedErrorMessage = "Email/password incorrect";

        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }
}
