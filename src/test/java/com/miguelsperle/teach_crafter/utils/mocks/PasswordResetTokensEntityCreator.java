package com.miguelsperle.teach_crafter.utils.mocks;

import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.PasswordResetTokensEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

import java.util.Date;

public class PasswordResetTokensEntityCreator {
    public static PasswordResetTokensEntity createValidPasswordResetToken(){
        return PasswordResetTokensEntity
                .builder()
                .id("1")
                .token("bT6zR8yQpD4xN7cE9vW2uM5sF1gH3jKl")
                .usersEntity(new UsersEntity())
                .expiresIn(new Date())
                .build();
    }

    public static PasswordResetTokensEntity createPasswordResetTokenToBeSaved(){
        return PasswordResetTokensEntity
                .builder()
                .id("1")
                .build();
    }

}
