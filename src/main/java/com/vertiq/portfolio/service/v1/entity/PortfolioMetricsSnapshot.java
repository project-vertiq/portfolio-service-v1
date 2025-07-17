package com.vertiq.portfolio.service.v1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "portfolio_metrics_snapshot")
public class PortfolioMetricsSnapshot {

    @EmbeddedId
    private PortfolioMetricsSnapshotId id;

    @Column(name = "total_value", nullable = false)
    private Double totalValue;

    @Column(name = "invested_value", nullable = false)
    private Double investedValue;

    @Column(name = "total_returns", nullable = false)
    private Double totalReturns;

    @Column(name = "todays_returns", nullable = false)
    private Double todaysReturns;

    @Column(name = "xirr")
    private Double xirr;

    @Column(name = "avg_market_cap")
    private Double avgMarketCap;

    @Column(name = "avg_pe_ratio")
    private Double avgPeRatio;

    @Column(name = "avg_pb_ratio")
    private Double avgPbRatio;

    @Column(name = "portfolio_alpha")
    private Double portfolioAlpha;

    @Column(name = "portfolio_beta")
    private Double portfolioBeta;

    @Column(name = "creation_datetime")
    private ZonedDateTime creationDatetime;

    @Column(name = "updation_datetime")
    private ZonedDateTime updationDatetime;

    @Column(name = "smallcap_percent")
    private Double smallcapPercent;

    @Column(name = "midcap_percent")
    private Double midcapPercent;

    @Column(name = "largecap_percent")
    private Double largecapPercent;

    @Column(name = "other_percent")
    private Double otherPercent;

    @Column(name = "industry_pe")
    private Double industryPe;

    @Column(name = "industry_pb")
    private Double industryPb;

    @Column(name = "div_yield")
    private Double divYield;

    @Column(name = "eps")
    private Double eps;

    @Column(name = "roe")
    private Double roe;

    @Column(name = "roce")
    private Double roce;

    @Column(name = "risk_label")
    private String riskLabel;
}
