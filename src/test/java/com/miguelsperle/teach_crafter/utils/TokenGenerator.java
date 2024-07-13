package com.miguelsperle.teach_crafter.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class TokenGenerator {
    public static String generateToken(UsersEntity user, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("teach-crafter")
                    .withSubject(user.getId())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    private static Instant genExpirationDate() {
        long expiration = 6 * 60 * 60 * 1000;  // 2 hours

        return LocalDateTime.now().plus(expiration, ChronoUnit.MILLIS).toInstant(ZoneOffset.of("-03:00"));
    }
}
