package com.miguelsperle.teach_crafter.modules.users.repositories;

import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.PasswordResetTokensEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokensRepository extends JpaRepository<PasswordResetTokensEntity, String> {
    Optional<PasswordResetTokensEntity> findByUsersEntityId(String id);
    Optional<PasswordResetTokensEntity> findByToken(String token);
}
