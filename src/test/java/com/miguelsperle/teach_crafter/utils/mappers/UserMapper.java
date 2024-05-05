package com.miguelsperle.teach_crafter.utils.mappers;

import com.miguelsperle.teach_crafter.modules.users.dtos.users.CreateUserDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

// A pasta mapper serve para guardar métodos para conversão de tipos
public class UserMapper {
    public static CreateUserDTO toConvertCreateUserDTO(UsersEntity usersEntity) {
        return new CreateUserDTO(usersEntity.getUsername(), usersEntity.getRole(), usersEntity.getName(), usersEntity.getEmail(), usersEntity.getPassword());
    }
}
