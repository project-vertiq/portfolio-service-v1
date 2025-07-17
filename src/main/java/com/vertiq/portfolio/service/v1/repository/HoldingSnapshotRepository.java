package com.vertiq.portfolio.service.v1.repository;

import com.vertiq.portfolio.service.v1.entity.HoldingSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HoldingSnapshotRepository extends JpaRepository<HoldingSnapshot, Long> {

    HoldingSnapshot findTopByHoldingIdOrderBySnapshotDateDesc(Long holdingId);

    List<HoldingSnapshot> findByHoldingIdIn(List<Long> holdingIds);
}
