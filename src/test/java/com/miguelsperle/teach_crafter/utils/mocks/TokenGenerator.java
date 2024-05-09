package com.miguelsperle.teach_crafter.utils.mocks;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class TokenGenerator {
    public static String generateToken(String id, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("aa")
                    .withSubject(id)
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
