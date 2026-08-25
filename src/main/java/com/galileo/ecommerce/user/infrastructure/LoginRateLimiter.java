package com.galileo.ecommerce.user.infrastructure;

import com.galileo.ecommerce.common.domain.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final int ATTEMPTS_PER_MINUTE = 5;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public void checkAttempt(String email, String clientIp) {
        String key = email.trim().toLowerCase(Locale.ROOT) + "|" + clientIp;
        Bucket bucket = buckets.computeIfAbsent(key, ignored -> newBucket());
        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException("too many login attempts, try again in a minute");
        }
    }

    private Bucket newBucket() {
        return Bucket.builder()
            .addLimit(Bandwidth.builder()
                .capacity(ATTEMPTS_PER_MINUTE)
                .refillGreedy(ATTEMPTS_PER_MINUTE, Duration.ofMinutes(1))
                .build())
            .build();
    }
}
