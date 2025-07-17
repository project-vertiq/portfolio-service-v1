package com.vertiq.portfolio.service.v1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vertiq.portfolio.service.v1.entity.PortfolioSectorAllocation;

@Repository
public interface PortfolioSectorDistributionRepository extends JpaRepository<PortfolioSectorAllocation, Long> {

    List<PortfolioSectorAllocation> findByPortfolioId(String portfolioId);

    List<PortfolioSectorAllocation> findByPortfolioIdIn(List<String> portfolioIds);
}
