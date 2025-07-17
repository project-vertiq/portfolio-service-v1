package com.vertiq.portfolio.service.v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vertiq.portfolio.service.v1.entity.PortfolioMetricsSnapshot;
import com.vertiq.portfolio.service.v1.entity.PortfolioMetricsSnapshotId;

@Repository
public interface PortfolioMetricsSnapshotRepository extends JpaRepository<PortfolioMetricsSnapshot, PortfolioMetricsSnapshotId> {

    PortfolioMetricsSnapshot findTopByIdPortfolioIdOrderByIdDatePartDesc(String portfolioId);
}
