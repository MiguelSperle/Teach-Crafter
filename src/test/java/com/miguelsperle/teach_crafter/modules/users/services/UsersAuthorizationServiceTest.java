package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.infra.security.TokenService;
import com.miguelsperle.teach_crafter.modules.users.dtos.authorization.UsersAuthorizationDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserPasswordMismatchException;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import com.miguelsperle.teach_crafter.utils.TokenGenerator;
import com.miguelsperle.teach_crafter.utils.unit.mocks.UsersEntityCreator;
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
public class UsersAuthorizationServiceTest {
    @InjectMocks
    private UsersAuthorizationService usersAuthorizationService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Test
    @DisplayName("Should be able to login with valid account credentials")
    public void should_be_able_to_login_with_valid_account_credentials(){
        when(this.usersRepository.findByEmail(any())).thenReturn(Optional.of(UsersEntityCreator.createValidAuthenticatedUsersEntity()));

        when(this.passwordEncoder.matches(any(), any())).thenReturn(true);

        String generatedToken = TokenGenerator.generateToken(UsersEntityCreator.createValidAuthenticatedUsersEntity(), "secret_key_test");

        when(this.tokenService.generateToken(any())).thenReturn(generatedToken);

        UsersAuthorizationDTO usersAuthorizationDTO = new UsersAuthorizationDTO(UsersEntityCreator.createUsersEntityToLogin().getEmail(), UsersEntityCreator.createUsersEntityToLogin().getPassword());

        String token = this.usersAuthorizationService.usersAuthorization(usersAuthorizationDTO);

        assertNotNull(token);
        assertEquals(generatedToken, token);
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("Should not be able to login with an incorrect email")
    public void should_not_be_able_to_login_with_an_incorrect_email(){
        when(this.usersRepository.findByEmail(any())).thenReturn(Optional.empty());

        UsersAuthorizationDTO usersAuthorizationDTO = new UsersAuthorizationDTO(UsersEntityCreator.createUsersEntityToLogin().getEmail(), UsersEntityCreator.createUsersEntityToLogin().getPassword());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            this.usersAuthorizationService.usersAuthorization(usersAuthorizationDTO);
        });

        String expectedErrorMessage = "Email and/or password incorrect";

        assertInstanceOf(UsernameNotFoundException.class, exception);
        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }

    @Test
    @DisplayName("Should not be able to login with an incorrect password")
    public void should_not_be_able_to_login_with_an_incorrect_password(){
        when(this.usersRepository.findByEmail(any())).thenReturn(Optional.of(UsersEntityCreator.createValidAuthenticatedUsersEntity()));

        when(this.passwordEncoder.matches(any(), any())).thenReturn(false);

        UsersAuthorizationDTO usersAuthorizationDTO = new UsersAuthorizationDTO(UsersEntityCreator.createUsersEntityToLogin().getEmail(), UsersEntityCreator.createUsersEntityToLogin().getPassword());

        UserPasswordMismatchException exception = assertThrows(UserPasswordMismatchException.class, () -> {
            this.usersAuthorizationService.usersAuthorization(usersAuthorizationDTO);
        });

        String expectedErrorMessage = "Email and/or password incorrect";

        assertInstanceOf(UserPasswordMismatchException.class, exception);
        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }
}
