package com.vertiq.portfolio.service.v1.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "holdings_snapshot")
public class HoldingSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "snapshot_id")
    private Long snapshotId;

    @Column(name = "holding_id", nullable = false)
    private Long holdingId;

    @Column(name = "date_part", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "day_change")
    private Double dayChange;

    @Column(name = "day_change_pct")
    private Double dayChangePct;

    @Column(name = "total_pnl")
    private Double totalPnl;

    @Column(name = "total_pnl_pct")
    private Double totalPnlPct;

    @Column(name = "market_price")
    private Double marketPrice;

    @Column(name = "pe_ratio")
    private Double peRatio;

    @Column(name = "pb_ratio")
    private Double pbRatio;

    @Column(name = "market_cap")
    private Double marketCap;

    @Column(name = "invested_value")
    private Double investedValue;

    @Column(name = "current_value")
    private Double currentValue;

    @Column(name = "creation_datetime")
    private ZonedDateTime creationDatetime;

    @Column(name = "updation_datetime")
    private ZonedDateTime updationDatetime;
}
