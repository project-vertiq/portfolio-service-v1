package com.vertiq.portfolio.service.v1.resource;

import com.vertiq.portfolio.service.v1.api.PortfolioApi;
import com.vertiq.portfolio.service.v1.controller.PortfolioSectorBreakdownController;
import com.vertiq.portfolio.service.v1.controller.PortfolioSummaryController;
import com.vertiq.portfolio.service.v1.model.PortfolioSectorBreakdownResponse;
import com.vertiq.portfolio.service.v1.model.PortfolioSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortfolioResource implements PortfolioApi {

    @Autowired
    PortfolioSummaryController portfolioSummaryController;

    @Autowired
    PortfolioSectorBreakdownController portfolioSectorBreakdownController;

    @Override
    public ResponseEntity<PortfolioSummary> getPortfolioSummary(String userId) {
        return new ResponseEntity<>(portfolioSummaryController.getPortfolioSummary(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PortfolioSectorBreakdownResponse> getSectorBreakdown(String userId) {
        return new ResponseEntity<>(portfolioSectorBreakdownController.getSectorBreakdown(userId), HttpStatus.OK);
    }
}
