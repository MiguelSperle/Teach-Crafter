package com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens;

import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Table(name = "password_reset_tokens")
@Entity(name = "password_reset_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetTokensEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String token;

    @Column(name = "expires_in", nullable = false)
    private Date expiresIn;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity usersEntity;
}
