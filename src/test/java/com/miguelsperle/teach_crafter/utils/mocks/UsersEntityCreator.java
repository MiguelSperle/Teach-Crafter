package com.miguelsperle.teach_crafter.utils.mocks;

import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

public class UsersEntityCreator {
    public static UsersEntity createValidUsersEntity(){
        return UsersEntity
                .builder()
                .id("1")
                .name("James")
                .email("james123@gmail.com")
                .username("james_captain")
                .password("$2bç02$Zn5ue7Tl1Mg/SQ7vpIAI8OeNNv3l.UWwF2t7A.cRw3ZQftEil7FZG") // test12345
                .build();
    }

    public static UsersEntity createUsersEntityToBeSaved(){
        return UsersEntity
                .builder()
                .id("1")
                .build();
    }

    public static UsersEntity createUsersEntityToLogin(){
        return UsersEntity
                .builder()
                .email("james123@gmail.com")
                .password("test12345")
                .build();
    }

    public static UsersEntity createUsersEntityToUpdateName() {
        return UsersEntity
                .builder()
                .name("LebronJames")
                .build();
    }

    public static UsersEntity createUsersEntityToUpdateUsername(){
        return UsersEntity
                .builder()
                .username("james_rossy")
                .build();
    }

    public static UsersEntity createUsersEntityToUpdateEmail(){
        return UsersEntity
                .builder()
                .email("lebronjames123@gmail.com")
                .build();
    }

    public static UsersEntity createUsersEntityToUpdatePassword(){
        return UsersEntity
                .builder()
                .password("v2Test12345")
                .build();
    }
}
