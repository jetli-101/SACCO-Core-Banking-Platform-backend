package com.example.sacco_core_banking.services;

import java.util.UUID;

import com.example.sacco_core_banking.entities.AuditLog;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.repositories.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void record(User actor, String action, String entityName, UUID entityId) {
        AuditLog log = new AuditLog();
        log.setUser(actor);
        log.setAction(action);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        auditLogRepository.save(log);
    }
}
