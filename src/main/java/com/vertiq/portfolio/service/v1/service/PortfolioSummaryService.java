package com.vertiq.portfolio.service.v1.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vertiq.portfolio.service.v1.entity.PortfolioMetricsSnapshot;
import com.vertiq.portfolio.service.v1.entity.UserBrokerAccount;
import com.vertiq.portfolio.service.v1.model.MarketCapData;
import com.vertiq.portfolio.service.v1.model.PortfolioMetrics;
import com.vertiq.portfolio.service.v1.model.PortfolioOverview;
import com.vertiq.portfolio.service.v1.model.PortfolioSummary;
import com.vertiq.portfolio.service.v1.repository.PortfolioMetricsSnapshotRepository;
import com.vertiq.portfolio.service.v1.repository.UserBrokerAccountRepository;

@Service
public class PortfolioSummaryService {

    @Autowired
    private PortfolioMetricsSnapshotRepository portfolioMetricsSnapshotRepository;
    @Autowired
    private UserBrokerAccountRepository userBrokerAccountRepository;

    private List<String> getPortfolioIdsForUser(String userId) {
        try {
            UUID userUUID = UUID.fromString(userId);
            List<UserBrokerAccount> accounts = userBrokerAccountRepository.findByUserId(userUUID);
            return accounts.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsActive()))
                    .map(UserBrokerAccount::getPortfolioId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public PortfolioSummary getPortfolioSummary(String userId) {
        List<String> portfolioIds = getPortfolioIdsForUser(userId);
        if (portfolioIds.isEmpty()) {
            return null;
        }
        List<PortfolioMetricsSnapshot> latestSnapshots = portfolioIds.stream()
                .map(pid -> portfolioMetricsSnapshotRepository.findTopByIdPortfolioIdOrderByIdDatePartDesc(pid))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (latestSnapshots.isEmpty()) {
            return null;
        }
        BigDecimal totalCurrentValue = sum(latestSnapshots, PortfolioMetricsSnapshot::getTotalValue);
        BigDecimal totalInvestedValue = sum(latestSnapshots, PortfolioMetricsSnapshot::getInvestedValue);
        BigDecimal totalReturns = sum(latestSnapshots, PortfolioMetricsSnapshot::getTotalReturns);
        BigDecimal todaysReturns = sum(latestSnapshots, PortfolioMetricsSnapshot::getTodaysReturns);
        BigDecimal sumCurrentValue = totalCurrentValue;
        BigDecimal avgMarketCap = weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getAvgMarketCap, sumCurrentValue);
        BigDecimal avgPeRatio = weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getAvgPeRatio, sumCurrentValue);
        BigDecimal avgPbRatio = weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getAvgPbRatio, sumCurrentValue);
        BigDecimal portfolioAlpha = weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getPortfolioAlpha, sumCurrentValue);
        BigDecimal portfolioBeta = weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getPortfolioBeta, sumCurrentValue);
        BigDecimal xirr = weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getXirr, sumCurrentValue);
        BigDecimal smallcapPercent = weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getSmallcapPercent, sumCurrentValue);
        BigDecimal midcapPercent = weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getMidcapPercent, sumCurrentValue);
        BigDecimal largecapPercent = weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getLargecapPercent, sumCurrentValue);
        BigDecimal otherPercent = weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getOtherPercent, sumCurrentValue);

        PortfolioOverview overview = new PortfolioOverview();
        overview.setCurrentValue(round2(totalCurrentValue));
        overview.setInvestedValue(round2(totalInvestedValue));
        overview.setTotalReturns(round2(totalReturns));
        overview.setTotalReturnsPercent(totalInvestedValue.compareTo(BigDecimal.ZERO) != 0 ? round2(totalReturns.divide(totalInvestedValue, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))) : BigDecimal.ZERO);
        overview.setTodaysReturns(round2(todaysReturns));
        // Correct day change percent: (todaysReturns / (totalCurrentValue - todaysReturns)) * 100
        BigDecimal yesterdayValue = totalCurrentValue.subtract(todaysReturns);
        overview.setTodaysReturnsPercent(
                yesterdayValue.compareTo(BigDecimal.ZERO) != 0
                ? round2(todaysReturns.divide(yesterdayValue, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)))
                : BigDecimal.ZERO
        );
        overview.setXirr(round2(xirr));

        PortfolioMetrics metrics = new PortfolioMetrics();
        metrics.setAvgMarketCap(round2(avgMarketCap));
        metrics.setAvgPE(round2(avgPeRatio));
        metrics.setPriceBook(round2(avgPbRatio));
        metrics.setAlpha(round2(portfolioAlpha));
        metrics.setBeta(round2(portfolioBeta));
        metrics.setIndustryPe(round2(weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getIndustryPe, sumCurrentValue)));
        metrics.setIndustryPb(round2(weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getIndustryPb, sumCurrentValue)));
        metrics.setDivYield(round2(weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getDivYield, sumCurrentValue)));
        metrics.setEps(round2(weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getEps, sumCurrentValue)));
        metrics.setRoe(round2(weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getRoe, sumCurrentValue)));
        metrics.setRoce(round2(weightedAvg(latestSnapshots, PortfolioMetricsSnapshot::getRoce, sumCurrentValue)));
        // Risk label logic based on portfolioAlpha
        String riskLabel;
        if (portfolioAlpha.compareTo(BigDecimal.valueOf(1.5)) < 0) {
            riskLabel = "Low";
        } else if (portfolioAlpha.compareTo(BigDecimal.valueOf(2.0)) < 0) {
            riskLabel = "Moderate";
        } else if (portfolioAlpha.compareTo(BigDecimal.valueOf(2.5)) < 0) {
            riskLabel = "High";
        } else {
            riskLabel = "Very High";
        }
        metrics.setRiskLabel(riskLabel);

        MarketCapData marketCapData = new MarketCapData();
        marketCapData.setSmallcapPercent(round2(smallcapPercent));
        marketCapData.setMidcapPercent(round2(midcapPercent));
        marketCapData.setLargecapPercent(round2(largecapPercent));
        marketCapData.setOtherPercent(round2(otherPercent));

        PortfolioSummary summary = new PortfolioSummary();
        summary.setOverview(overview);
        summary.setMetrics(metrics);
        summary.setMarketCapData(marketCapData);
        return summary;
    }

    private BigDecimal sum(List<PortfolioMetricsSnapshot> snapshots, java.util.function.Function<PortfolioMetricsSnapshot, Double> getter) {
        return snapshots.stream()
                .map(s -> Optional.ofNullable(getter.apply(s)).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal weightedAvg(List<PortfolioMetricsSnapshot> snapshots, java.util.function.Function<PortfolioMetricsSnapshot, Double> metricGetter, BigDecimal sumCurrentValue) {
        if (sumCurrentValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal weightedSum = snapshots.stream()
                .map(s -> Optional.ofNullable(metricGetter.apply(s)).orElse(0.0) * Optional.ofNullable(s.getTotalValue()).orElse(0.0))
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return weightedSum.divide(sumCurrentValue, 6, RoundingMode.HALF_UP);
    }

    private BigDecimal round2(BigDecimal value) {
        return value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
    }
}
