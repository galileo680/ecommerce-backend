package com.galileo.ecommerce.user.infrastructure;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class TokenConfig {

    @Bean
    JwtEncoder jwtEncoder(JwtProperties properties) {
        SecretKey key = new SecretKeySpec(Base64.getDecoder().decode(properties.secret()), "HmacSHA256");
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }
}
