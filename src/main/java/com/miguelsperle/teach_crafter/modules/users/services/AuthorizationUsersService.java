package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.infra.security.TokenService;
import com.miguelsperle.teach_crafter.modules.users.dtos.authorization.AuthorizationUsersDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.PasswordNotMatchUserException;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationUsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public String authorizationUsers(AuthorizationUsersDTO authorizationUsersDTO){
        UsersEntity users = this.usersRepository.findByEmail(authorizationUsersDTO.email())
                .orElseThrow(() -> new UsernameNotFoundException("Email/password incorrect"));

        this.verificationPasswordMatch(authorizationUsersDTO.password(), users.getPassword());

        return this.tokenService.generateToken(users);
    }

    private void verificationPasswordMatch(String passwordSender, String currentPassword){
        boolean passwordMatches = this.passwordEncoder.matches(passwordSender, currentPassword);

        if(!passwordMatches) throw new PasswordNotMatchUserException("Email/password incorrect");
    }
}
