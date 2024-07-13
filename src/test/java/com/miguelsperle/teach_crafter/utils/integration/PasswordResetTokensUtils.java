package com.miguelsperle.teach_crafter.utils.integration;

import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.PasswordResetTokensEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

import java.util.Date;

public class PasswordResetTokensUtils {
    public static PasswordResetTokensEntity createPasswordResetToken(UsersEntity user, String token, Date expiresIn) {
        return PasswordResetTokensEntity.builder().usersEntity(user).token(token).expiresIn(expiresIn).build();
    }
}
