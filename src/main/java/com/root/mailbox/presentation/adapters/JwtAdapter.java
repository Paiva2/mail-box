package com.root.mailbox.presentation.adapters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.root.mailbox.presentation.dto.jwt.GenerateJwtDto;
import com.root.mailbox.presentation.exceptions.JwtCreationException;
import com.root.mailbox.presentation.exceptions.JwtValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtAdapter {
    @Value("${jwt.secret}")
    private String SECRET;

    private static final String ISSUER = "MAIL_BOX_APP";
    private static final Integer ONE_DAY_IN_SECONDS = 60 * 60 * 24;

    public String generate(GenerateJwtDto input) {
        try {
            String token = JWT.create()
                .withIssuer(ISSUER)
                .withClaim("ID", input.getId())
                .withClaim("ROLE", input.getRole())
                .withSubject(input.getId().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(tokenExpirationTime())
                .sign(algorithm());

            return token;
        } catch (JWTCreationException exception) {
            throw new JwtCreationException();
        } catch (Exception ex) {
            throw new RuntimeException("Error while creating the JWT token");
        }
    }

    private DecodedJWT verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm())
                .withIssuer(ISSUER)
                .withClaimPresence("ID")
                .withClaimPresence("ROLE")
                .build();

            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw new JwtValidationException(exception.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Error while validating the JWT token");
        }
    }

    private Algorithm algorithm() {
        return Algorithm.HMAC256(SECRET);
    }

    private Instant tokenExpirationTime() {
        ZoneId utc = ZoneId.of("UTC");

        return Instant.now().plusSeconds(ONE_DAY_IN_SECONDS).atZone(utc).toInstant();
    }
}
