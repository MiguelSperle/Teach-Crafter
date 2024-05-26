package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.dtos.cloudinary.UploadImageModelDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.users.*;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserPasswordMismatchException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserAlreadyExistsException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CloudinaryImageService cloudinaryImageService;

    public UsersEntity getUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return (UsersEntity) authentication.getPrincipal();
        }

        return null;
    }

    public UsersEntity createUser(CreateUserDTO createUserDTO) {
        UsersEntity newUser = new UsersEntity();

        this.verifyUserAlreadyExists(createUserDTO.username(), createUserDTO.email());

        final String AVATAR_URL_IMAGE = "https://res.cloudinary.com/dnsxuxnto/image/upload/v1691878181/bm6z0rap3mkstebtopol.png";

        newUser.setUsername(createUserDTO.username());
        newUser.setRole(createUserDTO.role());
        newUser.setName(createUserDTO.name());
        newUser.setEmail(createUserDTO.email());
        newUser.setPassword(passwordEncoder.encode(createUserDTO.password()));
        newUser.setAvatarUrl(AVATAR_URL_IMAGE);

        return this.usersRepository.save(newUser);
    }

    private Optional<UsersEntity> getUserByUsernameOrEmail(String username, String email) {
        return this.usersRepository.findByUsernameOrEmail(username, email);
    }

    public Optional<UsersEntity> getUserByEmail(String email) {
        return this.usersRepository.findByEmail(email);
    }

    private Optional<UsersEntity> getUserByUsername(String username) {
        return this.usersRepository.findByUsername(username);
    }

    public UsersEntity getUserById(String userId) {
        return this.usersRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private void verifyUserAlreadyExists(String username, String email) {
        Optional<UsersEntity> user = this.getUserByUsernameOrEmail(username, email);

        if (user.isPresent()) throw new UserAlreadyExistsException("User already exists");
    }

    public void updateNameUser(UpdateNameUserDTO updateNameUserDTO) {
        UsersEntity user = this.getUserAuthenticated();

        user.setName(updateNameUserDTO.newName());

        this.usersRepository.save(user);
    }

    public void updateUsernameUser(UpdateUsernameUserDTO updateUsernameUserDTO) {
        UsersEntity user = this.getUserAuthenticated();

        this.verifyUserAlreadyExistsByUsername(updateUsernameUserDTO.newUsername());

        this.verifyPasswordMatch(updateUsernameUserDTO.currentPassword(), user.getPassword());

        user.setUsername(updateUsernameUserDTO.newUsername());

        this.usersRepository.save(user);
    }

    private void verifyUserAlreadyExistsByUsername(String username) {
        Optional<UsersEntity> user = this.getUserByUsername(username);

        if (user.isPresent()) throw new UserAlreadyExistsException("This username is already used");
    }

    private void verifyPasswordMatch(String passwordSender, String currentPassword) {
        boolean passwordMatches = this.passwordEncoder.matches(passwordSender, currentPassword);

        if (!passwordMatches) throw new UserPasswordMismatchException("Incorrect password");
    }

    public void updateEmailUser(UpdateEmailUserDTO updateEmailUserDTO) {
        UsersEntity user = this.getUserAuthenticated();

        this.verifyUserAlreadyExistsByNewEmail(updateEmailUserDTO.newEmail());

        this.verifyPasswordMatch(updateEmailUserDTO.currentPassword(), user.getPassword());

        user.setEmail(updateEmailUserDTO.newEmail());

        this.usersRepository.save(user);
    }

    private void verifyUserAlreadyExistsByNewEmail(String email) {
        Optional<UsersEntity> user = this.getUserByEmail(email);

        if (user.isPresent()) throw new UserAlreadyExistsException("This email is already used");
    }

    public void updatePasswordUserLogged(UpdatePasswordUserLoggedDTO updatePasswordUserLoggedDTO) {
        UsersEntity user = this.getUserAuthenticated();

        this.verifyPasswordMatch(updatePasswordUserLoggedDTO.currentPassword(), user.getPassword());

        user.setPassword(this.passwordEncoder.encode(updatePasswordUserLoggedDTO.newPassword()));

        this.usersRepository.save(user);
    }

    public void updateImageUser(UploadImageModelDTO uploadImageModelDTO) {
        UsersEntity user = this.getUserAuthenticated();

        user.setAvatarUrl(this.cloudinaryImageService.uploadImageFile(uploadImageModelDTO.imageFile(), "profile_pics"));

        this.usersRepository.save(user);
    }


    public void save(UsersEntity user) {
        this.usersRepository.save(user);
    }
}
