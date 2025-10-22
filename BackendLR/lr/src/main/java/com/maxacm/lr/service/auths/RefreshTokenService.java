package com.maxacm.lr.service.auths;

import com.maxacm.lr.entity.RefreshToken;
import com.maxacm.lr.repository.auths.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepository repo;

    public RefreshToken create(String username, String token, LocalDateTime expiry) {
        RefreshToken rt = new RefreshToken(null, username, token, expiry);
        return repo.save(rt);
    }

    public boolean validate(String token) {
        return repo.findByToken(token)
                .filter(rt -> rt.getExpiryDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public void invalidate(String token) {
        repo.deleteByToken(token);
    }

    @Scheduled(cron = "0 0 * * * *") // cada hora
    public void cleanExpiredTokens() {
        repo.findAll().stream()
                .filter(rt -> rt.getExpiryDate().isBefore(LocalDateTime.now()))
                .forEach(rt -> repo.delete(rt));
    }
}
