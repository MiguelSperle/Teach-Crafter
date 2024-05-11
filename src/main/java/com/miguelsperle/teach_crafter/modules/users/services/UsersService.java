package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.dtos.cloudinary.UploadImageModelDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.users.*;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.PasswordNotMatchUserException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserAlreadyExistsException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    public UsersEntity getUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return (UsersEntity) authentication.getPrincipal();
        }

        return null;
    }

    public UsersEntity createUser(CreateUserDTO createUserDTO){
        UsersEntity newUser = new UsersEntity();

        this.verificationAlreadyExistsUser(createUserDTO.username(), createUserDTO.email());

        final String AVATAR_URL_IMAGE = "https://res.cloudinary.com/dnsxuxnto/image/upload/v1691878181/bm6z0rap3mkstebtopol.png";

        newUser.setUsername(createUserDTO.username());
        newUser.setRole(createUserDTO.role());
        newUser.setName(createUserDTO.name());
        newUser.setEmail(createUserDTO.email());
        newUser.setPassword(passwordEncoder.encode(createUserDTO.password()));
        newUser.setAvatar(AVATAR_URL_IMAGE);

        return this.usersRepository.save(newUser);
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
        return this.usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
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

        this.verificationPasswordMatch(updateUsernameUserDTO.currentPassword(), loggedUser.getPassword());

        loggedUser.setUsername(updateUsernameUserDTO.newUsername());

        this.usersRepository.save(loggedUser);
    }

    private void verificationAlreadyExistsUserByNewUsername(String username){
        Optional<UsersEntity> user = this.getUserByUsername(username);

        if(user.isPresent()) throw new UserAlreadyExistsException("This username is already used");
    }

    private void verificationPasswordMatch(String passwordSender, String currentPassword){
        boolean passwordMatches = this.passwordEncoder.matches(passwordSender, currentPassword);

        if(!passwordMatches) throw new PasswordNotMatchUserException("Incorrect password");
    }

    public void updateEmailUser(UpdateEmailUserDTO updateEmailUserDTO){
        UsersEntity loggedUser = this.getUserAuthenticated();

        this.verificationAlreadyExistsUserByNewEmail(updateEmailUserDTO.newEmail());

        this.verificationPasswordMatch(updateEmailUserDTO.currentPassword(), loggedUser.getPassword());

        loggedUser.setEmail(updateEmailUserDTO.newEmail());

        this.usersRepository.save(loggedUser);
    }

    private void verificationAlreadyExistsUserByNewEmail(String email){
        Optional<UsersEntity> user = this.getUserByEmail(email);

        if(user.isPresent()) throw new UserAlreadyExistsException("This email is already used");
    }

    public void updatePasswordUserLogged(UpdatePasswordUserLoggedDTO updatePasswordUserLoggedDTO) {
        UsersEntity loggedUser = this.getUserAuthenticated();

        this.verificationPasswordMatch(updatePasswordUserLoggedDTO.currentPassword(), loggedUser.getPassword());

        loggedUser.setPassword(this.passwordEncoder.encode(updatePasswordUserLoggedDTO.newPassword()));

        this.usersRepository.save(loggedUser);
    }

    public void updateImageUser(UploadImageModelDTO uploadImageModelDTO) {
        UsersEntity loggedUser = this.getUserAuthenticated();

        loggedUser.setAvatar(this.cloudinaryService.uploadFile(uploadImageModelDTO.file(), "profile_pics"));

        this.usersRepository.save(loggedUser);
    }


    public void save(UsersEntity user){
        this.usersRepository.save(user);
    }
}
