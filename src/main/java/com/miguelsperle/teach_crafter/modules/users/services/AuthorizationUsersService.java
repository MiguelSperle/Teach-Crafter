package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.infra.security.TokenService;
import com.miguelsperle.teach_crafter.modules.users.dtos.authorization.AuthorizationUsersDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserPasswordMismatchException;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationUsersService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;

    public String authorizationUsers(AuthorizationUsersDTO authorizationUsersDTO) {
        UsersEntity user = this.usersRepository.findByEmail(authorizationUsersDTO.email())
                .orElseThrow(() -> new UsernameNotFoundException("Email/password incorrect"));

        this.verifyPasswordMatch(authorizationUsersDTO.password(), user.getPassword());

        return this.tokenService.generateToken(user);
    }

    private void verifyPasswordMatch(String passwordSender, String currentPassword) {
        boolean passwordMatches = this.passwordEncoder.matches(passwordSender, currentPassword);

        if (!passwordMatches) throw new UserPasswordMismatchException("Email/password incorrect");
    }
}
