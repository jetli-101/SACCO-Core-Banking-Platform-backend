package com.example.sacco_core_banking.entities;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import com.example.sacco_core_banking.enums.WorkFlowInstanceStatus;
import com.example.sacco_core_banking.enums.WorkFlowPriority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * One running (or finished) process for a single business record — a specific claim, loan
 * application, leave request, etc. `data` is the live payload the owning module cares
 * about; it travels with the instance and is mutated on every transition so downstream
 * stages always see the accumulated data, not just a reference id.
 */
@Entity
@Table(name = "smoothsurf_sacco_wf_instances")
@Getter
@Setter
@NoArgsConstructor
public class WorkFlowInstance extends BaseEntity {

    @NotBlank
    @Column(name = "process_code", nullable = false, unique = true)
    private String processCode;

    @Column(name = "process_name")
    private String processName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workflow_id", nullable = false)
    private WorkFlow workFlow;

    @NotBlank
    @Column(name = "process_type_key", nullable = false)
    private String processTypeKey;

    @NotNull
    @Column(name = "reference_id", nullable = false)
    private UUID referenceId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "current_stage_id", nullable = false)
    private WorkFlowStage currentStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_status_id")
    private WorkFlowStatus currentStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkFlowInstanceStatus status = WorkFlowInstanceStatus.INITIATED;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkFlowPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedTo;

    @Column(name = "due_date")
    private OffsetDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiated_by_user_id")
    private User initiatedBy;

    @Column(name = "initiated_at", nullable = false)
    private OffsetDateTime initiatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> data;
}
