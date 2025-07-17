package com.vertiq.portfolio.service.v1.repository;


import com.vertiq.portfolio.service.v1.entity.UserBrokerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserBrokerAccountRepository extends JpaRepository<UserBrokerAccount, String> {

    List<UserBrokerAccount> findByUserId(UUID userId);
}
