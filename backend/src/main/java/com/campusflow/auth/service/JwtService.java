package com.campusflow.auth.service;

import com.campusflow.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String secret;
    private final long expirySeconds;

    public JwtService(
            @Value("${campusflow.jwt.secret:campusflow-local-development-secret-change-me}") String secret,
            @Value("${campusflow.jwt.expiry-seconds:86400}") long expirySeconds) {
        this.secret = secret;
        this.expirySeconds = expirySeconds;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{"
                + "\"sub\":\"" + escape(user.getEmail()) + "\","
                + "\"userId\":" + user.getId() + ","
                + "\"name\":\"" + escape(user.getName()) + "\","
                + "\"role\":\"" + user.getRole().getName().name() + "\","
                + "\"department\":\"" + escape(user.getDepartment() == null ? "" : user.getDepartment().getName()) + "\","
                + "\"iat\":" + now.getEpochSecond() + ","
                + "\"exp\":" + now.plusSeconds(expirySeconds).getEpochSecond()
                + "}";

        String unsignedToken = base64Url(header) + "." + base64Url(payload);
        return unsignedToken + "." + sign(unsignedToken);
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to create JWT token", ex);
        }
    }

    private String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
