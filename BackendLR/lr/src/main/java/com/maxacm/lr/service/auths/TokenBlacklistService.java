package com.maxacm.lr.service.auths;

import com.maxacm.lr.dto.blacklisttoken.BlacklistedTokenDTO;
import com.maxacm.lr.entity.BlacklistedToken;
import com.maxacm.lr.repository.auths.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final BlacklistedTokenRepository repo;

    public BlacklistedTokenDTO toDTO(BlacklistedToken blacklistedToken) {
        return BlacklistedTokenDTO.builder()
                .id(blacklistedToken.getId())
                .token(blacklistedToken.getToken())
                .createdAt(blacklistedToken.getCreatedAt())
                .build();
    }

    public void blacklistToken(String token) {
        repo.save(new BlacklistedToken(null, token, LocalDateTime.now()));
    }

    public boolean isBlacklisted(String token) {
        return repo.existsByToken(token);
    }

    public List<BlacklistedTokenDTO> getAll(){
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
