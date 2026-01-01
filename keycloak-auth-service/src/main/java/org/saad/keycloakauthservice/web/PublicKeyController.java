package org.saad.keycloakauthservice.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Controller to expose Keycloak public key and JWK set.
 * This allows other services to validate JWT tokens without directly accessing Keycloak.
 */
@RestController
@RequestMapping("/api")
public class PublicKeyController {

    @Value("${keycloak.issuer-uri}")
    private String issuerUri;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Get the JWK Set (JSON Web Key Set) from Keycloak.
     * This contains the public keys used to validate JWT tokens.
     */
    @GetMapping("/jwk-set")
    public ResponseEntity<Map<String, Object>> getJwkSet() {
        try {
            String jwkSetUri = issuerUri + "/protocol/openid-connect/certs";
            Map<String, Object> jwkSet = restTemplate.getForObject(jwkSetUri, Map.class);
            if (jwkSet == null) {
                return ResponseEntity.status(503).body(Map.of("error", "Keycloak not available"));
            }
            return ResponseEntity.ok(jwkSet);
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                    "error", "Failed to fetch JWK Set from Keycloak",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Get the public key information.
     * Simplified endpoint that returns the issuer URI and JWK Set URI.
     */
    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> getPublicKeyInfo() {
        return ResponseEntity.ok(Map.of(
                "issuer", issuerUri,
                "jwks_uri", issuerUri + "/protocol/openid-connect/certs"
        ));
    }
}

