package com.miguelsperle.teach_crafter.modules.users.repository;

import com.miguelsperle.teach_crafter.modules.users.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<UsersEntity, String> {
    Optional<UsersEntity> findByUsernameOrEmail(String username, String email);
}
