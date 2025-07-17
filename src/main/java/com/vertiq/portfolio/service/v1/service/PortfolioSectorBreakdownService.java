package com.vertiq.portfolio.service.v1.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.vertiq.portfolio.service.v1.model.PortfolioSectorBreakdownResponse;
import com.vertiq.portfolio.service.v1.model.SectorBreakdownEntry;
import com.vertiq.portfolio.service.v1.model.SectorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vertiq.portfolio.service.v1.entity.PortfolioSectorAllocation;
import com.vertiq.portfolio.service.v1.entity.UserBrokerAccount;
import com.vertiq.portfolio.service.v1.repository.PortfolioSectorDistributionRepository;
import com.vertiq.portfolio.service.v1.repository.UserBrokerAccountRepository;

@Service
public class PortfolioSectorBreakdownService {

    @Autowired
    private PortfolioSectorDistributionRepository sectorDistributionRepository;
    @Autowired
    private UserBrokerAccountRepository userBrokerAccountRepository;

    public PortfolioSectorBreakdownResponse getSectorBreakdown(String userId) {
        List<String> portfolioIds = userBrokerAccountRepository.findByUserId(UUID.fromString(userId))
                .stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsActive()))
                .map(UserBrokerAccount::getPortfolioId)
                .collect(Collectors.toList());
        if (portfolioIds.isEmpty()) {
            return new PortfolioSectorBreakdownResponse(Collections.emptyList());
        }
        List<PortfolioSectorAllocation> all = sectorDistributionRepository.findByPortfolioIdIn(portfolioIds);
        Map<String, List<PortfolioSectorAllocation>> byPortfolio = all.stream().collect(Collectors.groupingBy(PortfolioSectorAllocation::getPortfolioId));
        // Cumulative sector breakdown for all portfolios combined (after all per-portfolio logic)
        List<SectorData> result = new ArrayList<>();
        if (!portfolioIds.isEmpty()) {
            Map<String, BigDecimal> cumulativeValueMap = new java.util.HashMap<>();
            BigDecimal totalCumulativeValue = BigDecimal.ZERO;
            for (PortfolioSectorAllocation alloc : all) {
                String sector = alloc.getSector();
                BigDecimal value = round2BigDecimal(alloc.getAllocationValue());
                cumulativeValueMap.put(sector, cumulativeValueMap.getOrDefault(sector, BigDecimal.ZERO).add(value));
                totalCumulativeValue = totalCumulativeValue.add(value);
            }
            List<SectorBreakdownEntry> cumulativeEntries = new ArrayList<>();
            BigDecimal percentSum = BigDecimal.ZERO;
            for (Map.Entry<String, BigDecimal> entry : cumulativeValueMap.entrySet()) {
                BigDecimal value = round2BigDecimal(entry.getValue().doubleValue());
                BigDecimal percent = totalCumulativeValue.compareTo(BigDecimal.ZERO) > 0 ? value.divide(totalCumulativeValue, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
                percent = percent.setScale(2, RoundingMode.HALF_UP);
                SectorBreakdownEntry entryObj = new SectorBreakdownEntry();
                entryObj.setName(entry.getKey());
                entryObj.setValue(value);
                entryObj.setPercent(percent);
                cumulativeEntries.add(entryObj);
                percentSum = percentSum.add(percent);
            }
            if (percentSum.subtract(BigDecimal.valueOf(100)).abs().compareTo(BigDecimal.valueOf(0.01)) > 0) {
                BigDecimal otherPercent = round2BigDecimal(BigDecimal.valueOf(100).subtract(percentSum).doubleValue());
                BigDecimal otherValue = round2BigDecimal(totalCumulativeValue.multiply(otherPercent).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP).doubleValue());
                SectorBreakdownEntry otherEntry = new SectorBreakdownEntry();
                otherEntry.setName("Other");
                otherEntry.setValue(otherValue);
                otherEntry.setPercent(otherPercent);
                cumulativeEntries.add(otherEntry);
            }
            SectorData allData = new SectorData();
            allData.setPortfolioId("ALL");
            allData.setSectors(cumulativeEntries);
            result.add(allData);
        }
        return new PortfolioSectorBreakdownResponse(result);
    }

    private BigDecimal round2BigDecimal(Double value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
