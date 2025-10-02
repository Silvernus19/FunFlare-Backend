package com.funflare.funflare.repository;

import com.funflare.funflare.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    boolean findByUser_id(Long userId);
    Optional<Wallet> findByWalletId(Long walletId);
    Optional<Wallet> findByUserId(Long userId);


    boolean existsByUser_Id(Long userId);
}
