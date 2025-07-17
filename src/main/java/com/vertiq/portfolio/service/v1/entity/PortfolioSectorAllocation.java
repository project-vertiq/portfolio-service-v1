package com.vertiq.portfolio.service.v1.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "portfolio_sector_allocation")
public class PortfolioSectorAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "portfolio_id", nullable = false)
    private String portfolioId;

    @Column(name = "sector", nullable = false)
    private String sector;

    @Column(name = "allocation_value", nullable = false)
    private Double allocationValue;

    @Column(name = "allocation_percent", nullable = false)
    private Double allocationPercent;

    @Column(name = "creation_datetime")
    private ZonedDateTime creationDatetime;

    @Column(name = "updation_datetime")
    private ZonedDateTime updationDatetime;
}
