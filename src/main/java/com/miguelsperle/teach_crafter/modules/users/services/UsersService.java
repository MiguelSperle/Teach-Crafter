package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.dtos.users.*;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserAlreadyExistsException;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersEntity getUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return (UsersEntity) authentication.getPrincipal();
        }

        return null;
    }

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

    private Optional<UsersEntity> getUserByUsernameOrEmail(String username, String email){
        return this.usersRepository.findByUsernameOrEmail(username,email);
    }

    public Optional<UsersEntity> getUserByEmail(String email){
        return this.usersRepository.findByEmail(email);
    }

    private Optional<UsersEntity> getUserByUsername(String username){
        return this.usersRepository.findByUsername(username);
    }

    public UsersEntity getUserById(String id){
        return this.usersRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private void verificationAlreadyExistsUser(String username, String email){
        Optional<UsersEntity> user = this.getUserByUsernameOrEmail(username, email);

        if(user.isPresent()) throw new UserAlreadyExistsException("User already exists");
    }

    public void updateNameUser(UpdateNameUserDTO updateNameUserDTO){
        UsersEntity loggedUser = this.getUserAuthenticated();

        loggedUser.setName(updateNameUserDTO.newName());

        this.usersRepository.save(loggedUser);
    }

    public void updateUsernameUser(UpdateUsernameUserDTO updateUsernameUserDTO){
        UsersEntity loggedUser = this.getUserAuthenticated();

        this.verificationAlreadyExistsUserByNewUsername(updateUsernameUserDTO.newUsername());

        loggedUser.setUsername(updateUsernameUserDTO.newUsername());

        this.usersRepository.save(loggedUser);
    }

    private void verificationAlreadyExistsUserByNewUsername(String username){
        Optional<UsersEntity> user = this.getUserByUsername(username);

        if(user.isPresent()) throw new UserAlreadyExistsException("This username is already used");
    }

    public void updateEmailUser(UpdateEmailUserDTO updateEmailUserDTO){
        UsersEntity loggedUser = this.getUserAuthenticated();

        this.verificationAlreadyExistsUserByNewEmail(updateEmailUserDTO.newEmail());

        loggedUser.setEmail(updateEmailUserDTO.newEmail());

        this.usersRepository.save(loggedUser);
    }

    private void verificationAlreadyExistsUserByNewEmail(String email){
        Optional<UsersEntity> user = this.getUserByEmail(email);

        if(user.isPresent()) throw new UserAlreadyExistsException("This email is already used");
    }

    public void save(UsersEntity user){
        this.usersRepository.save(user);
    }
}
