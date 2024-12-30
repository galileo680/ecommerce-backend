package com.bartek.ecommerce.repository;

import com.bartek.ecommerce.entity.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Integer> {

    Optional<ActivationToken> findByToken(String token);
}
