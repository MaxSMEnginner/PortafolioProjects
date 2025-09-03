package com.maxacm.lr.service;

import com.maxacm.lr.entity.AuditLog;
import com.maxacm.lr.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository  auditLogRepository;

    public void log(String username, String action, String ip) {
        AuditLog log = AuditLog.builder()
                .username(username)
                .action(action)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }

    public List<AuditLog> getAll() {
        return auditLogRepository.findAll();
    }

    public List<AuditLog> getByUser(String username) {
        return auditLogRepository.findByUsername(username);
    }

    public List<AuditLog> getByAction(String action) {
        return auditLogRepository.findByAction(action);
    }
}
