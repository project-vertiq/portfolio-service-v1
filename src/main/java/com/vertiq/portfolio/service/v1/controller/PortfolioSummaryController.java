package com.vertiq.portfolio.service.v1.controller;

import com.vertiq.portfolio.service.v1.model.PortfolioSummary;
import com.vertiq.portfolio.service.v1.service.PortfolioSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortfolioSummaryController {

    @Autowired
    private PortfolioSummaryService portfolioSummaryService;

    public PortfolioSummary getPortfolioSummary(String userId) {
        return portfolioSummaryService.getPortfolioSummary(userId);
    }
}
