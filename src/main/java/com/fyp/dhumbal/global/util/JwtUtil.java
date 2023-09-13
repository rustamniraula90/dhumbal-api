package com.fyp.dhumbal.global.util;

import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.ForbiddenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private JwtUtil() {
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JwtToken {
        private String token;
        private LocalDateTime expiry;
    }

    public static class Generator {
        private Generator() {
        }

        public static JwtToken generate(String id, String subject, Map<String, Object> claims, int expirationInMinutes,
                                        String key) {
            LocalDateTime date = LocalDateTime.now().plusMinutes(expirationInMinutes);
            SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), SignatureAlgorithm.HS512.getJcaName());
            JwtBuilder token = Jwts.builder().setId(id).setSubject(subject).setExpiration(Date.from(date.atZone(ZoneId.of("UTC")).toInstant())).addClaims(claims)
                    .signWith(secretKey, SignatureAlgorithm.HS512);
            return new JwtToken(token.compact(), date);
        }
    }

    public static class Validator {
        private Validator() {
        }

        public static Claims parse(String token, String key) {
            try {
                SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), SignatureAlgorithm.HS512.getJcaName());
                return Jwts.parserBuilder().setClock(() -> Date.from(Instant.now())).setSigningKey(secretKey).build()
                        .parseClaimsJws(token).getBody();
            } catch (SignatureException | MalformedJwtException e) {
                throw new ForbiddenException(ErrorCodes.TOKEN_NOT_VALID,
                        "Token signature verification / parsing failed");
            } catch (ExpiredJwtException e) {
                throw new ForbiddenException(ErrorCodes.TOKEN_EXPIRED, "Token has expired");
            }
        }

        public static Claims parse(String token, RSAPublicKey secretKey) {
            try {
                return Jwts.parserBuilder().setClock(() -> Date.from(Instant.now())).setSigningKey(secretKey).build()
                        .parseClaimsJws(token).getBody();
            } catch (SignatureException | MalformedJwtException e) {
                throw new ForbiddenException(ErrorCodes.TOKEN_NOT_VALID,
                        "Token signature verification / parsing failed");
            } catch (ExpiredJwtException e) {
                throw new ForbiddenException(ErrorCodes.TOKEN_EXPIRED, "Token has expired");
            }
        }
    }
}
