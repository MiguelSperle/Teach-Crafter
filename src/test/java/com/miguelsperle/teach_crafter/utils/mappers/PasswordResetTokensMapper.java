package com.miguelsperle.teach_crafter.utils.mappers;

import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetTokens.CreatePasswordResetTokenDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetTokens.ResetPasswordUserNotLoggedDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

public class PasswordResetTokensMapper {
    public static CreatePasswordResetTokenDTO toConvertCreatePasswordResetTokenDTO(UsersEntity usersEntity){
        return new CreatePasswordResetTokenDTO(usersEntity.getEmail());
    }

    public static ResetPasswordUserNotLoggedDTO toConvertResetPasswordUserNotLoggedDTO(UsersEntity usersEntity, String token){
        return new ResetPasswordUserNotLoggedDTO(usersEntity.getPassword(), token);
    }
}
