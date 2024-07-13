package com.miguelsperle.teach_crafter.utils.integration;

import com.github.javafaker.Faker;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

public class UsersUtils {
    private static final String AVATAR_URL_IMAGE = "https://res.cloudinary.com/dnsxuxnto/image/upload/v1691878181/bm6z0rap3mkstebtopol.png";

    public static UsersEntity createUser(String role, String password, Faker faker) {
        return UsersEntity.builder().username(faker.name().username()).name(faker.name().name())
                .role(role).email(faker.internet().emailAddress()).password(password).avatarUrl(AVATAR_URL_IMAGE).build();
    }
}
