package com.miguelsperle.teach_crafter.utils.mocks;

import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

import java.time.LocalDateTime;

public class UsersEntityCreator {
    public static UsersEntity createValidAuthenticatedUsersEntity() {
        return UsersEntity
                .builder()
                .id("1")
                .name("James")
                .email("james123@gmail.com")
                .username("james_captain")
                .password("$2bç02$Zn5ue7Tl1Mg/SQ7vpIAI8OeNNv3l.UWwF2t7A.cRw3ZQftEil7FZG") // test12345
                .avatarUrl("IMAGE_URL")
                .createdAt(LocalDateTime.now().minusHours(2))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .build();
    }

    public static UsersEntity createSecondValidUsersEntity() {
        return UsersEntity
                .builder()
                .id("2")
                .name("Jayson")
                .email("jayson@gmail.com")
                .username("jayson_tatum")
                .password("$c33e29fc98a4d5n329fas292c8a4930739a17f8f39c7b415f2471b15770158") // test1234567
                .avatarUrl("IMAGE_URL")
                .createdAt(LocalDateTime.now().minusHours(2))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .build();
    }

    public static UsersEntity createUsersEntityToBeSaved() {
        return UsersEntity
                .builder()
                .id("1")
                .build();
    }

    public static UsersEntity createUsersEntityToLogin() {
        return UsersEntity
                .builder()
                .email("james123@gmail.com")
                .password("test12345")
                .build();
    }

    public static UsersEntity createUsersEntityToUpdateName() {
        return UsersEntity
                .builder()
                .name("Lebron")
                .build();
    }

    public static UsersEntity createUsersEntityToUpdateUsername() {
        return UsersEntity
                .builder()
                .username("jayson_tatum")
                .build();
    }

    public static UsersEntity createUsersEntityToUpdateEmail() {
        return UsersEntity
                .builder()
                .email("jayson@gmail.com")
                .build();
    }

    public static UsersEntity createUsersEntityToUpdatePassword() {
        return UsersEntity
                .builder()
                .password("v2Test12345")
                .build();
    }

    public static UsersEntity createValidCurrentPasswordAuthenticatedUsersEntity() {
        return UsersEntity
                .builder()
                .password("test12345")
                .build();
    }
}
