package com.vertiq.portfolio.service.v1.resource;

import com.vertiq.portfolio.service.v1.api.HoldingsApi;
import com.vertiq.portfolio.service.v1.controller.HoldingsController;
import com.vertiq.portfolio.service.v1.model.HoldingsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HoldingsResource implements HoldingsApi {

    @Autowired
    HoldingsController holdingsController;

    @Override
    public ResponseEntity<HoldingsResponse> getAllHoldings(String userId) {
        return new ResponseEntity<>(holdingsController.getUserHoldings(userId), HttpStatus.OK);
    }
}
