package com.galileo.ecommerce.user.application;

import com.galileo.ecommerce.common.domain.BusinessRuleException;
import com.galileo.ecommerce.user.domain.RefreshToken;
import com.galileo.ecommerce.user.domain.User;
import com.galileo.ecommerce.user.infrastructure.JwtProperties;
import com.galileo.ecommerce.user.infrastructure.JwtTokenService;
import com.galileo.ecommerce.user.infrastructure.LoginRateLimiter;
import com.galileo.ecommerce.user.infrastructure.RefreshTokenRepository;
import com.galileo.ecommerce.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final LoginRateLimiter loginRateLimiter;

    public UUID register(String email, String password, String firstName, String lastName) {
        String normalized = User.normalizeEmail(email);
        if (users.existsByEmail(normalized)) {
            throw new BusinessRuleException("email %s is already registered".formatted(normalized));
        }
        User user = User.register(normalized, passwordEncoder.encode(password), firstName, lastName);
        return users.save(user).getId();
    }

    public TokenPair login(String email, String password, String clientIp) {
        loginRateLimiter.checkAttempt(email, clientIp);
        User user = users.findByEmail(User.normalizeEmail(email))
            .orElseThrow(() -> new BadCredentialsException("bad credentials"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("bad credentials");
        }
        return issueTokens(user);
    }

    public TokenPair refresh(String refreshTokenValue) {
        RefreshToken stored = refreshTokens.findByTokenHash(hash(refreshTokenValue))
            .orElseThrow(() -> new BadCredentialsException("unknown refresh token"));
        refreshTokens.delete(stored);
        if (stored.isExpired(Instant.now())) {
            throw new BadCredentialsException("expired refresh token");
        }
        User user = users.findById(stored.getAccountId())
            .orElseThrow(() -> new BadCredentialsException("unknown account"));
        return issueTokens(user);
    }

    public void logout(String refreshTokenValue) {
        refreshTokens.deleteByTokenHash(hash(refreshTokenValue));
    }

    private TokenPair issueTokens(User user) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String refreshValue = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Instant expiresAt = Instant.now().plus(jwtProperties.refreshTtl());
        refreshTokens.save(new RefreshToken(user.getId(), hash(refreshValue), expiresAt));
        return new TokenPair(jwtTokenService.issueAccessToken(user), refreshValue);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
