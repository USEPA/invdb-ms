package gov.epa.ghg.invdb.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;

@ConfigurationProperties(prefix = "jwt")
public record RsaKeyConfigProperties(Resource publicKey, Resource privateKey) {
    public RSAPublicKey rsaPublicKey() {
        try {
            RSAKey key = loadResource(publicKey);
            return key.toRSAPublicKey();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate rsa public key", e);
        }
    }

    public RSAPrivateKey rsaPrivateKey() {
        try {
            RSAKey key = loadResource(privateKey);
            return key.toRSAPrivateKey();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate rsa private key", e);
        }
    }

    private RSAKey loadResource(Resource resource) {
        try (InputStream keyResource = resource.getInputStream()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(keyResource))) {
                String keyContent = reader.lines().collect(Collectors.joining(""));
                return RSAKey.parse(keyContent);
            } catch (ParseException e) {
                throw new RuntimeException("Failed to read or parse key", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or parse key", e);
        }

    }
}
