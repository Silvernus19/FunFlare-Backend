// PurchaseController.java
package com.funflare.funflare.controller;

import com.funflare.funflare.dto.PurchaseCreateDTO;
import com.funflare.funflare.model.Purchases;
import com.funflare.funflare.model.User;
import com.funflare.funflare.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/create")
    public ResponseEntity<?> createPurchase(
            @Valid @RequestBody PurchaseCreateDTO dto,
            @RequestParam Long userId  // ← Add this
    ) {
        try {
            Purchases purchase = purchaseService.createPurchase(dto, userId);
            return ResponseEntity.ok(purchase);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}