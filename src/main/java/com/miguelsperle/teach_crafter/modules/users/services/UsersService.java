package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.dtos.CreateUserDTO;
import com.miguelsperle.teach_crafter.modules.users.entity.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.entity.exceptions.UserAlreadyExistsException;
import com.miguelsperle.teach_crafter.modules.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(CreateUserDTO createUserDTO){
        UsersEntity newUser = new UsersEntity();

        this.verificationAlreadyExistsUser(createUserDTO.username(), createUserDTO.email());

        final String AVATAR_URL_IMAGE = "https://res.cloudinary.com/dnsxuxnto/image/upload/v1691878181/bm6z0rap3mkstebtopol.png";

        newUser.setUsername(createUserDTO.username());
        newUser.setRole(createUserDTO.role());
        newUser.setName(createUserDTO.name());
        newUser.setEmail(createUserDTO.email());
        newUser.setPassword(passwordEncoder.encode(createUserDTO.password()));
        newUser.setAvatar(AVATAR_URL_IMAGE);

        this.usersRepository.save(newUser);
    }

    public Optional<UsersEntity> getUserByUsernameOrEmail(String username, String email){
        return this.usersRepository.findByUsernameOrEmail(username,email);
    }

    private void verificationAlreadyExistsUser(String username, String email){
        Optional<UsersEntity> user = this.getUserByUsernameOrEmail(username, email);

        if(user.isPresent()) throw new UserAlreadyExistsException("User already exists");
    }
}
