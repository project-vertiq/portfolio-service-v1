package com.vertiq.portfolio.service.v1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PortfolioMetricsSnapshotId implements Serializable {

    @Column(name = "portfolio_id", nullable = false)
    private String portfolioId;

    @Column(name = "date_part", nullable = false)
    private LocalDate datePart;
}
