package com.example.sacco_core_banking.services;

import java.util.UUID;

import com.example.sacco_core_banking.entities.AuditLog;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void record(User actor, String action, String entityName, UUID entityId) {
        AuditLog log = new AuditLog();
        log.setUser(actor);
        log.setAction(action);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        auditLogRepository.save(log);
    }
}
