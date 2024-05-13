package com.miguelsperle.teach_crafter.utils.mappers;

import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetToken.CreatePasswordResetTokenDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetToken.ResetPasswordUserNotLoggedDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

public class PasswordResetTokenMapper {
    public static CreatePasswordResetTokenDTO toConvertCreatePasswordResetTokenDTO(UsersEntity usersEntity){
        return new CreatePasswordResetTokenDTO(usersEntity.getEmail());
    }

    public static ResetPasswordUserNotLoggedDTO toConvertResetPasswordUserNotLoggedDTO(UsersEntity usersEntity, String token){
        return new ResetPasswordUserNotLoggedDTO(usersEntity.getPassword(), token);
    }
}
