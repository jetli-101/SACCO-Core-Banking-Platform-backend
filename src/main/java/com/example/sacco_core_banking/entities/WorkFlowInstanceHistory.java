package com.example.sacco_core_banking.entities;

import java.time.OffsetDateTime;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Append-only audit trail: one row per transition taken on a WorkFlowInstance.
 * dataSnapshot captures the instance's full data payload exactly as it stood right after
 * this transition, so the history shows what the record looked like at each step, not just
 * what changed.
 */
@Entity
@Table(name = "smoothsurf_sacco_wf_instance_history")
@Getter
@Setter
@NoArgsConstructor
public class WorkFlowInstanceHistory extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instance_id", nullable = false)
    private WorkFlowInstance instance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_stage_id")
    private WorkFlowStage fromStage;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_stage_id", nullable = false)
    private WorkFlowStage toStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transition_id")
    private WorkFlowTransition transition;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_user_id")
    private User performedBy;

    @Column(name = "performed_at", nullable = false)
    private OffsetDateTime performedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data_snapshot", columnDefinition = "jsonb")
    private Map<String, Object> dataSnapshot;
}
