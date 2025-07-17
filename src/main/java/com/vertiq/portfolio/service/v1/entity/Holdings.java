package com.vertiq.portfolio.service.v1.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "holdings")
public class Holdings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "holding_id")
    private Long holdingId;

    @Column(name = "portfolio_id", nullable = false)
    private String portfolioId;

    @Column(name = "isin", nullable = false)
    private String isin;

    @Column(name = "exchange", nullable = false)
    private String exchange;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @Column(name = "avg_price")
    private Double avgPrice;

    @Column(name = "holding_type")
    private String holdingType;

    @Column(name = "creation_datetime")
    private ZonedDateTime creationDatetime;

    @Column(name = "updation_datetime")
    private ZonedDateTime updationDatetime;
}
