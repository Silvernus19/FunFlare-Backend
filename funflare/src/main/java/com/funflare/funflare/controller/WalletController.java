package com.funflare.funflare.controller;


import com.funflare.funflare.dto.WalletCreateDTO;
import com.funflare.funflare.dto.WalletResponseDTO;
import com.funflare.funflare.model.Wallet;
import com.funflare.funflare.repository.WalletRepository;
import com.funflare.funflare.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Users/")
public class WalletController {
    private WalletService  walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("create/wallet")
    public ResponseEntity<WalletResponseDTO> createWallet( @Valid @RequestBody WalletCreateDTO  dto) {
        Wallet wallet = walletService.createWallet(dto);
        WalletResponseDTO Response= new WalletResponseDTO(wallet);
        return new ResponseEntity<>(Response, HttpStatus.CREATED);
    }
}
