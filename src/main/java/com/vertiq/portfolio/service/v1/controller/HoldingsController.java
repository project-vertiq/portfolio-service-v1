package com.vertiq.portfolio.service.v1.controller;

import com.vertiq.portfolio.service.v1.model.HoldingsResponse;
import com.vertiq.portfolio.service.v1.service.HoldingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HoldingsController {

    @Autowired
    private HoldingsService holdingsService;

    public HoldingsResponse getUserHoldings(String userId) {
        return holdingsService.getUserHoldings(userId);
    }
}
