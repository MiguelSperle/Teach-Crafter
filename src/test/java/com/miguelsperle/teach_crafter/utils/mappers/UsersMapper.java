package com.miguelsperle.teach_crafter.utils.mappers;

import com.miguelsperle.teach_crafter.modules.users.dtos.authorization.AuthorizationUsersDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.users.*;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

public class UsersMapper {
    public static CreateUserDTO toConvertCreateUserDTO(UsersEntity usersEntity) {
        return new CreateUserDTO(
                usersEntity.getUsername(),
                usersEntity.getRole(),
                usersEntity.getName(),
                usersEntity.getEmail(),
                usersEntity.getPassword()
        );
    }

    public static AuthorizationUsersDTO toConvertAuthorizationUsersDTO(UsersEntity usersEntity){
        return new AuthorizationUsersDTO(usersEntity.getEmail(), usersEntity.getPassword());
    }

    public static UpdateNameUserDTO toConvertUpdateNameUserDTO(UsersEntity usersEntity){
        return new UpdateNameUserDTO(usersEntity.getName());
    }

    public static UpdateUsernameUserDTO toConvertUpdateUsernameUserDTO(UsersEntity usersEntity, String currentPassword){
        return new UpdateUsernameUserDTO(usersEntity.getUsername(), currentPassword);
    }

    public static UpdateEmailUserDTO toConvertUpdateEmailUserDTO(UsersEntity usersEntity, String currentPassword){
        return new UpdateEmailUserDTO(usersEntity.getEmail(), currentPassword);
    }

    public static UpdatePasswordUserLoggedDTO toConvertUpdatePasswordUserLoggedDTO(UsersEntity usersEntity, String currentPassword){
        return new UpdatePasswordUserLoggedDTO(usersEntity.getPassword(), currentPassword);
    }
}
