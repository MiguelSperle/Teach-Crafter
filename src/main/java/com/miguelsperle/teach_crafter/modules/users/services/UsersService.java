package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.dtos.users.*;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserPasswordMismatchException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserAlreadyExistsException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryImageService cloudinaryImageService;

    public UsersService(
            final UsersRepository usersRepository,
            final PasswordEncoder passwordEncoder,
            final CloudinaryImageService cloudinaryImageService
    ) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryImageService = cloudinaryImageService;
    }

    public UsersEntity getAuthenticatedUser() {
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

        newUser.setUsername(createUserDTO.username().toLowerCase());
        newUser.setRole(createUserDTO.role());
        newUser.setName(createUserDTO.name());
        newUser.setEmail(createUserDTO.email());
        newUser.setPassword(this.passwordEncoder.encode(createUserDTO.password()));
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

    public void updateUserName(UpdateUserNameDTO updateUserNameDTO) {
        UsersEntity user = this.getAuthenticatedUser();

        user.setName(updateUserNameDTO.newName());

        this.usersRepository.save(user);
    }

    public void updateUserUsername(UpdateUserUsernameDTO updateUserUsernameDTO) {
        UsersEntity user = this.getAuthenticatedUser();

        this.verifyUserAlreadyExistsByUsername(updateUserUsernameDTO.newUsername());

        this.verifyPasswordMatch(updateUserUsernameDTO.currentPassword(), user.getPassword());

        user.setUsername(updateUserUsernameDTO.newUsername());

        this.usersRepository.save(user);
    }

    private void verifyUserAlreadyExistsByUsername(String username) {
        Optional<UsersEntity> user = this.getUserByUsername(username);

        if (user.isPresent()) throw new UserAlreadyExistsException("This username is already used");
    }

    private void verifyPasswordMatch(String passwordSent, String currentPassword) {
        boolean passwordMatches = this.passwordEncoder.matches(passwordSent, currentPassword);

        if (!passwordMatches) throw new UserPasswordMismatchException("Incorrect current password");
    }

    public void updateUserEmail(UpdateUserEmailDTO updateUserEmailDTO) {
        UsersEntity user = this.getAuthenticatedUser();

        this.verifyUserAlreadyExistsByNewEmail(updateUserEmailDTO.newEmail());

        this.verifyPasswordMatch(updateUserEmailDTO.currentPassword(), user.getPassword());

        user.setEmail(updateUserEmailDTO.newEmail());

        this.usersRepository.save(user);
    }

    private void verifyUserAlreadyExistsByNewEmail(String email) {
        Optional<UsersEntity> user = this.getUserByEmail(email);

        if (user.isPresent()) throw new UserAlreadyExistsException("This email is already used");
    }

    public void updateLoggedUserPassword(UpdateLoggedUserPasswordDTO updateLoggedUserPasswordDTO) {
        UsersEntity user = this.getAuthenticatedUser();

        this.verifyPasswordMatch(updateLoggedUserPasswordDTO.currentPassword(), user.getPassword());

        user.setPassword(this.passwordEncoder.encode(updateLoggedUserPasswordDTO.newPassword()));

        this.usersRepository.save(user);
    }

    public void updateUserImage(MultipartFile imageFile) {
        UsersEntity user = this.getAuthenticatedUser();

        user.setAvatarUrl(this.cloudinaryImageService.uploadImageFile(imageFile, "profile_pics"));

        this.usersRepository.save(user);
    }


    public void save(UsersEntity user) {
        this.usersRepository.save(user);
    }
}
