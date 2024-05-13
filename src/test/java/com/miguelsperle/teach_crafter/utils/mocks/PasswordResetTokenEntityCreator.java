package com.miguelsperle.teach_crafter.utils.mocks;

import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.PasswordResetTokenEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

import java.util.Date;

public class PasswordResetTokenEntityCreator {
    public static PasswordResetTokenEntity createValidPasswordResetToken(){
        return PasswordResetTokenEntity
                .builder()
                .id("1")
                .token("bT6zR8yQpD4xN7cE9vW2uM5sF1gH3jKl")
                .usersEntity(new UsersEntity())
                .expiresIn(new Date())
                .build();
    }

    public static PasswordResetTokenEntity createPasswordResetTokenToBeSaved(){
        return PasswordResetTokenEntity
                .builder()
                .id("1")
                .build();
    }

}
