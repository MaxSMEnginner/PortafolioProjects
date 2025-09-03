package com.maxsoft.loginregister.repository;

import com.maxsoft.loginregister.entity.ListanegraToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListanegraRepository extends JpaRepository<ListanegraToken, Long> {
    boolean existsByToken(String token);
}
