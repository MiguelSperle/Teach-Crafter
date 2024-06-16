package com.miguelsperle.teach_crafter.utils.mappers;

import com.miguelsperle.teach_crafter.modules.users.dtos.authorization.UsersAuthorizationDTO;
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

    public static UsersAuthorizationDTO toConvertAuthorizationUsersDTO(UsersEntity usersEntity) {
        return new UsersAuthorizationDTO(usersEntity.getEmail(), usersEntity.getPassword());
    }

    public static UpdateUserNameDTO toConvertUpdateUserNameDTO(UsersEntity usersEntity) {
        return new UpdateUserNameDTO(usersEntity.getName());
    }

    public static UpdateUserUsernameDTO toConvertUpdateUserUsernameDTO(UsersEntity usersEntity, String currentPassword) {
        return new UpdateUserUsernameDTO(usersEntity.getUsername(), currentPassword);
    }

    public static UpdateUserEmailDTO toConvertUpdateUserEmailDTO(UsersEntity usersEntity, String currentPassword) {
        return new UpdateUserEmailDTO(usersEntity.getEmail(), currentPassword);
    }

    public static UpdateLoggedUserPasswordDTO toConvertUpdateLoggedUserPasswordDTO(UsersEntity usersEntity, String currentPassword) {
        return new UpdateLoggedUserPasswordDTO(usersEntity.getPassword(), currentPassword);
    }
}
