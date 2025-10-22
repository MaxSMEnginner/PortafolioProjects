package com.maxacm.lr.service.auths;
import com.maxacm.lr.dto.auditlog.AuditLogDTO;
import com.maxacm.lr.entity.AuditLog;
import com.maxacm.lr.repository.auths.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository  auditLogRepository;

    public AuditLogDTO toDTO(AuditLog auditLog){
        return AuditLogDTO.builder().
                id(auditLog.getId()).
                username(auditLog.getUsername()).
                action(auditLog.getAction()).
                ip(auditLog.getIp()).
                timestamp(auditLog.getTimestamp())
                .build();
    }

    public void log(String username, String action, String ip) {
        AuditLog log = AuditLog.builder()
                .username(username)
                .action(action)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }

    public List<AuditLogDTO> getAll() {
        return auditLogRepository.findAll()
                .stream().
                map(this::toDTO).
                collect(Collectors.toList());
    }

    public List<AuditLogDTO> getByUser(String username) {
        return auditLogRepository.findByUsername(username)
                .stream().
                map(this::toDTO).
                collect(Collectors.toList());

    }

    public List<AuditLogDTO> getByAction(String action) {
        return auditLogRepository.findByAction(action).stream().map(this::toDTO).collect(Collectors.toList());
    }
}
