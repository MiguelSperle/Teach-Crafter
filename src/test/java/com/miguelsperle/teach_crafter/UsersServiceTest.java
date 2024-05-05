package com.miguelsperle.teach_crafter;

import com.miguelsperle.teach_crafter.modules.users.dtos.users.CreateUserDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import com.miguelsperle.teach_crafter.modules.users.services.CloudinaryService;
import com.miguelsperle.teach_crafter.modules.users.services.UsersService;
import com.miguelsperle.teach_crafter.utils.mappers.UserMapper;
import com.miguelsperle.teach_crafter.utils.mocks.UsersEntityCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

        when(this.usersRepository.save(any(UsersEntity.class))).thenReturn(UsersEntityCreator.createUserEntityToBeSaved());

        CreateUserDTO convertedToCreateUserDTO = UserMapper.toConvertCreateUserDTO(UsersEntityCreator.createUserEntityToBeSaved());

        UsersEntity newUser = this.usersService.createUser(convertedToCreateUserDTO);

        assertNotNull(newUser.getId());
        assertThat(newUser).hasFieldOrProperty("id");
    }

    @Test
    @DisplayName("Should be not able to create an user account if one already exists")
    public void should_be_not_able_to_create_an_account_if_one_already_exists() {
        when(this.usersRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.of(UsersEntityCreator.createUserEntity()));

        CreateUserDTO convertedToCreateUserDTO = UserMapper.toConvertCreateUserDTO(UsersEntityCreator.createUserEntityToBeSaved());

        assertThatExceptionOfType(Exception.class)
                // The code that I hope will throw an exception
                .isThrownBy(() -> this.usersService.createUser(convertedToCreateUserDTO))
                .withMessageContaining("User already exists"); // Check the message that the exception will throw
    }
}
