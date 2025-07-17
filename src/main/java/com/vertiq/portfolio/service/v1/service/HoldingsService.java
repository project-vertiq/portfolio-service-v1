package com.vertiq.portfolio.service.v1.service;

import com.vertiq.portfolio.service.v1.model.HoldingsResponse;
import com.vertiq.portfolio.service.v1.model.Holding;
import com.vertiq.portfolio.service.v1.entity.Holdings;
import com.vertiq.portfolio.service.v1.entity.HoldingSnapshot;
import com.vertiq.portfolio.service.v1.entity.UserBrokerAccount;
import com.vertiq.portfolio.service.v1.repository.HoldingsRepository;
import com.vertiq.portfolio.service.v1.repository.HoldingSnapshotRepository;
import com.vertiq.portfolio.service.v1.repository.UserBrokerAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HoldingsService {

    @Autowired
    private HoldingsRepository holdingsRepository;
    @Autowired
    private HoldingSnapshotRepository holdingSnapshotRepository;
    @Autowired
    private UserBrokerAccountRepository userBrokerAccountRepository;

    public HoldingsResponse getUserHoldings(String userId) {
        List<UserBrokerAccount> accounts = userBrokerAccountRepository.findByUserId(UUID.fromString(userId));
        List<String> portfolioIds = accounts.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsActive()))
                .map(UserBrokerAccount::getPortfolioId)
                .collect(Collectors.toList());
        if (portfolioIds.isEmpty()) {
            HoldingsResponse response = new HoldingsResponse();
            response.setHoldings(Collections.emptyList());
            return response;
        }
        List<Holdings> holdingsList = holdingsRepository.findByPortfolioIdIn(portfolioIds);
        List<Long> holdingIds = holdingsList.stream().map(Holdings::getHoldingId).collect(Collectors.toList());
        List<HoldingSnapshot> allSnapshots = holdingSnapshotRepository.findByHoldingIdIn(holdingIds);
        // Map holdingId -> latest snapshot
        Map<Long, HoldingSnapshot> latestSnapshotMap = allSnapshots.stream()
                .collect(Collectors.toMap(
                        HoldingSnapshot::getHoldingId,
                        snap -> snap,
                        (snap1, snap2) -> snap1.getSnapshotDate().isAfter(snap2.getSnapshotDate()) ? snap1 : snap2
                ));
        // Group holdings by ISIN
        Map<String, List<Holdings>> holdingsByIsin = holdingsList.stream().collect(Collectors.groupingBy(Holdings::getIsin));
        List<Holding> holdingModels = new ArrayList<>();
        for (Map.Entry<String, List<Holdings>> entry : holdingsByIsin.entrySet()) {
            String isin = entry.getKey();
            List<Holdings> group = entry.getValue();
            // Aggregate quantity and weighted avg price
            double totalQty = group.stream().mapToDouble(h -> h.getQuantity() != null ? h.getQuantity() : 0.0).sum();
            double totalInvested = group.stream().mapToDouble(h -> (h.getQuantity() != null && h.getAvgPrice() != null) ? h.getQuantity() * h.getAvgPrice() : 0.0).sum();
            double weightedAvgPrice = totalQty > 0 ? totalInvested / totalQty : 0.0;
            // Use the first holding for static fields (name, symbol, type)
            Holdings h = group.get(0);
            // Aggregate snapshots for this ISIN
            List<HoldingSnapshot> groupSnaps = group.stream().map(hold -> latestSnapshotMap.get(hold.getHoldingId())).filter(Objects::nonNull).collect(Collectors.toList());
            double ltp = groupSnaps.stream().mapToDouble(s -> s.getMarketPrice() != null ? s.getMarketPrice() : 0.0).average().orElse(0.0);
            double currentValue = groupSnaps.stream().mapToDouble(s -> s.getCurrentValue() != null ? s.getCurrentValue() : 0.0).sum();
            double dayPnl = groupSnaps.stream().mapToDouble(s -> s.getDayChange() != null ? s.getDayChange() : 0.0).sum();
            double dayPercent = groupSnaps.stream().mapToDouble(s -> s.getDayChangePct() != null ? s.getDayChangePct() : 0.0).average().orElse(0.0);
            double overallPnl = groupSnaps.stream().mapToDouble(s -> s.getTotalPnl() != null ? s.getTotalPnl() : 0.0).sum();
            double overallPercent = groupSnaps.stream().mapToDouble(s -> s.getTotalPnlPct() != null ? s.getTotalPnlPct() : 0.0).average().orElse(0.0);
            Holding model = new Holding();
            model.setName(h.getTicker());
            model.setSymbol(h.getTicker());
            model.setType(h.getHoldingType());
            model.setQuantity(toBigDecimal(totalQty));
            model.setAvgPrice(toBigDecimal(weightedAvgPrice));
            model.setLtp(toBigDecimal(ltp));
            model.setCurrentValue(toBigDecimal(currentValue));
            model.setInvestedValue(toBigDecimal(totalInvested));
            model.setDayPnl(toBigDecimal(dayPnl));
            model.setDayPercent(toBigDecimal(dayPercent));
            model.setOverallPnl(toBigDecimal(overallPnl));
            model.setOverallPercent(toBigDecimal(overallPercent));
            holdingModels.add(model);
        }
        HoldingsResponse response = new HoldingsResponse();
        response.setHoldings(holdingModels);
        return response;
    }

    private BigDecimal toBigDecimal(Double value) {
        return value == null ? null : BigDecimal.valueOf(value);
    }
}
