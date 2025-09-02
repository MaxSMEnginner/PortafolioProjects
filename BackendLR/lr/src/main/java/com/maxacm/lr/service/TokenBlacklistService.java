package com.maxacm.lr.service;

import com.maxacm.lr.entity.BlacklistedToken;
import com.maxacm.lr.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final BlacklistedTokenRepository repo;

    public void blacklistToken(String token) {
        repo.save(new BlacklistedToken(null, token, LocalDateTime.now()));
    }

    public boolean isBlacklisted(String token) {
        return repo.existsByToken(token);
    }
}
