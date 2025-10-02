package com.funflare.funflare.service;

import com.funflare.funflare.dto.WalletCreateDTO;
import com.funflare.funflare.model.User;
import com.funflare.funflare.model.Wallet;
import com.funflare.funflare.repository.UserRepository;
import com.funflare.funflare.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);


    public WalletService(WalletRepository walletRepository,  PasswordEncoder passwordEncoder,  UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional
    public Wallet createWallet(WalletCreateDTO dto) {

//        validate user existence

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", dto.getUserId());
                    return new IllegalArgumentException("User with ID " + dto.getUserId() + " not found");
                });

//        check if wallet already exists for the user
//        boolean exists = walletRepository.findByUser_id(
//                dto.getUserId()
//        );
//        if (exists) {
//            logger.error("Wallet already exists");
//            throw new RuntimeException("Wallet already exists you can only have one wallet");
//        }

        if (walletRepository.existsByUser_Id((dto.getUserId()))) {
            logger.error("Wallet already exists for user ID {}", dto.getUserId());
            throw new IllegalStateException("Wallet already exists for user ID " + dto.getUserId());
        }

        Wallet wallet = new Wallet();
        wallet.setUserId(dto.getUserId());
        wallet.setWalletPin(passwordEncoder.encode(dto.getWalletPin()));

        Wallet savedWallet = walletRepository.save(wallet);
        logger.info("Wallet created successfully for user ID {}", dto.getUserId());

        return savedWallet;



    }
}
