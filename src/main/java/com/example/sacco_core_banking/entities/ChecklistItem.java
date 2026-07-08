package com.example.sacco_core_banking.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** One item belonging to a standalone Checklist. */
@Entity
@Table(name = "smoothsurf_sacco_checklist_items")
@Getter
@Setter
@NoArgsConstructor
public class ChecklistItem extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "checklist_id", nullable = false)
    @JsonIgnore
    private Checklist checklist;

    @NotBlank
    @Column(name = "item_text", nullable = false)
    private String itemText;

    @Column(nullable = false)
    private boolean required = true;

    @Column(name = "order_no")
    private int orderNo;
}
