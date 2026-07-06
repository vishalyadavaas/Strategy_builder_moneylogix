package com.moneylogix.strategybuilder.riskprofile;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RiskProfileRepository extends JpaRepository<RiskProfile, UUID> {
    Optional<RiskProfile> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
}
