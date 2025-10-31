package com.funflare.funflare.repository;

import com.funflare.funflare.model.Purchases;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchasesRepository extends JpaRepository <Purchases,Long> {
    // PurchasesRepository.java
    Optional<Purchases> findByTransactionRef(String transactionRef);



}
