package com.harrison.BankAPI.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.harrison.BankAPI.models.entity.Person;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  private final Algorithm algorithm;

  public TokenService(@Value("${api.security.token.secret}") String secret) {
    this.algorithm = Algorithm.HMAC256(secret);
  }

  public String generateToken(Person person) {
    return JWT.create()
        .withIssuer("bankapi")
        .withSubject(person.getUsername())
        .withExpiresAt(generateExpirationTime())
        .sign(algorithm);
  }

  private Instant generateExpirationTime() {
    return LocalDateTime.now()
        .plusMinutes(15)
        .toInstant(ZoneOffset.of("-03:00"));
  }

  public String validateToken(String token) {
    return JWT.require(algorithm)
        .withIssuer("bankapi")
        .build()
        .verify(token)
        .getSubject();
  }

}
