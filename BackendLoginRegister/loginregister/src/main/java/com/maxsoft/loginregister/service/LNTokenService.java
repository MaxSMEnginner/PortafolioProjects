package com.maxsoft.loginregister.service;


import com.maxsoft.loginregister.entity.ListanegraToken;
import com.maxsoft.loginregister.repository.ListanegraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LNTokenService {
    private final ListanegraRepository repo;

    public void AgregarTokenLN(String token){
        repo.save(new ListanegraToken(null, LocalDateTime.now(), token));
    }

    public boolean EstaenLNToken(String token){
        return repo.existsByToken(token);
    }

}
