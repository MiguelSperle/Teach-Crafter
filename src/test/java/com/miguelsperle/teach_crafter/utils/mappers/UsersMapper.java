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

    public static UpdateUsernameUserDTO toConvertUpdateUsernameUserDTO(UsersEntity usersEntity){
        return new UpdateUsernameUserDTO(usersEntity.getUsername(), null);
    }

    public static UpdateEmailUserDTO toConvertUpdateEmailUserDTO(UsersEntity usersEntity){
        return new UpdateEmailUserDTO(usersEntity.getEmail(), null);
    }

    public static UpdatePasswordUserLoggedDTO toConvertUpdatePasswordUserLoggedDTO(UsersEntity usersEntity){
        return new UpdatePasswordUserLoggedDTO(usersEntity.getPassword(), null);
    }
}
