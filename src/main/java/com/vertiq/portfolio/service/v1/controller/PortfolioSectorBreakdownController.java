package com.vertiq.portfolio.service.v1.controller;

import com.vertiq.portfolio.service.v1.model.PortfolioSectorBreakdownResponse;
import com.vertiq.portfolio.service.v1.service.PortfolioSectorBreakdownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortfolioSectorBreakdownController {

    @Autowired
    private PortfolioSectorBreakdownService portfolioSectorBreakdownService;

    public PortfolioSectorBreakdownResponse getSectorBreakdown(String userId) {
        return portfolioSectorBreakdownService.getSectorBreakdown(userId);
    }
}
