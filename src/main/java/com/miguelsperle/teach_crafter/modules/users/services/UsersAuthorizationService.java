package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.infra.security.TokenService;
import com.miguelsperle.teach_crafter.modules.users.dtos.authorization.UsersAuthorizationDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserPasswordMismatchException;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersAuthorizationService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UsersAuthorizationService(
            final UsersRepository usersRepository,
            final PasswordEncoder passwordEncoder,
            final TokenService tokenService
    ) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public String usersAuthorization(UsersAuthorizationDTO usersAuthorizationDTO) {
        UsersEntity user = this.usersRepository.findByEmail(usersAuthorizationDTO.email())
                .orElseThrow(() -> new UsernameNotFoundException("Email and/or password incorrect"));

        this.verifyPasswordMatch(usersAuthorizationDTO.password(), user.getPassword());

        return this.tokenService.generateToken(user);
    }

    private void verifyPasswordMatch(String passwordSender, String currentPassword) {
        boolean passwordMatches = this.passwordEncoder.matches(passwordSender, currentPassword);

        if (!passwordMatches) throw new UserPasswordMismatchException("Email and/or password incorrect");
    }
}
