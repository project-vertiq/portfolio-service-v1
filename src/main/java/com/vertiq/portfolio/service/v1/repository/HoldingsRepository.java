package com.vertiq.portfolio.service.v1.repository;

import com.vertiq.portfolio.service.v1.entity.Holdings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoldingsRepository extends JpaRepository<Holdings, Long> {

    List<Holdings> findByPortfolioIdIn(List<String> portfolioIds);
}
