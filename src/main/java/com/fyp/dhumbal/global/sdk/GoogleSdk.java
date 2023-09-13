package com.fyp.dhumbal.global.sdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.InternalServerException;
import com.fyp.dhumbal.global.property.GoogleProperty;
import com.fyp.dhumbal.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleSdk {

    private final GoogleProperty google;
    private final ObjectMapper objectMapper;

    private static final String KEY_ID_HEADER = "kid";
    private static final String CLIENT_ID = "aud";

    private Map<String, RSAPublicKey> certs = null;
    private LocalDateTime cacheExpiry = null;


    public GoogleUserDetail verifyCredential(String credential) {
        updateCerts();
        String keyId = getKeyIdFromCredential(credential);
        if (!certs.containsKey(keyId)) {
            throw new InternalServerException(ErrorCodes.GOOGLE_EXCEPTION, "Certificate with key id doesn't exists!");
        }
        Claims claims = JwtUtil.Validator.parse(credential, certs.get(keyId));

        if (!claims.get(CLIENT_ID, String.class).equals(google.getClientId())) {
            throw new InternalServerException(ErrorCodes.GOOGLE_EXCEPTION, "Client Id from token is invalid!");
        }

        return createGoogleUserDetail(claims);

    }

    private void updateCerts() {
        if (cacheExpiry == null || LocalDateTime.now().isAfter(cacheExpiry)) {
            certs = new HashMap<>();
            ResponseEntity<GoogleCert> response = WebClient.create(google.getCertUrl()).get()
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            err -> Mono.error(new InternalServerException(ErrorCodes.GOOGLE_EXCEPTION, "Error while getting certs from google!")))
                    .toEntity(GoogleCert.class)
                    .block();
            if (response == null || response.getHeaders().getCacheControl() == null || response.getBody() == null)
                throw new InternalServerException(ErrorCodes.GOOGLE_EXCEPTION, "Error while getting certs from google!");

            // cache-control = public, max-age=21543, must-revalidate, no-transform
            int cacheAge = Integer.parseInt(response.getHeaders().getCacheControl().split(",")[1].split("=")[1]);

            cacheExpiry = LocalDateTime.now().plusSeconds(cacheAge);
            for (GoogleJwk key : response.getBody().getKeys()) {
                certs.put(key.getKid(), createKey(key.getN(), key.getE()));
            }
        }
    }

    private String getKeyIdFromCredential(String credential) {
        try {
            return objectMapper
                    .readValue(new String(Base64.getDecoder().decode(credential.split("\\.")[0])), Map.class)
                    .get(KEY_ID_HEADER).toString();
        } catch (JsonProcessingException e) {
            log.error("Could not decode credential from Google", e);
            throw new InternalServerException(ErrorCodes.GOOGLE_EXCEPTION, "Error getting kid from token");
        }
    }

    private GoogleUserDetail createGoogleUserDetail(Claims claims) {
        String id = claims.getSubject();
        String name = claims.get("name", String.class);
        String email = claims.get("email", String.class);
        return new GoogleUserDetail(id, name, email);
    }

    @Getter
    @Setter
    public static class GoogleCert {
        private List<GoogleJwk> keys;
    }

    @Getter
    @Setter
    public static class GoogleJwk {
        private String e;
        private String n;
        private String alg;
        private String kid;
        private String kty;
        private String use;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GoogleUserDetail {
        private String id;
        private String name;
        private String email;
    }

    public static RSAPublicKey createKey(String n, String e) {
        try {
            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
            BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            log.error("Could not create SecretKey from JWK", ex);
            throw new InternalServerException(ErrorCodes.GOOGLE_EXCEPTION, "Could not create SecretKey from JWK");
        }
    }

}
