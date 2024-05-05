package com.miguelsperle.teach_crafter.utils.mocks;

import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import java.util.UUID;

public class UsersEntityCreator {
    public static UsersEntity createUserEntityToBeSaved(){
        return UsersEntity
                .builder()
                .id(String.valueOf(UUID.randomUUID()))
                .build();
    }

    public static UsersEntity createUserEntity(){
        return UsersEntity
                .builder()
                .id(String.valueOf(UUID.randomUUID()))
                .email("test123@gmail.com")
                .username("test")
                .build();
    }
}
