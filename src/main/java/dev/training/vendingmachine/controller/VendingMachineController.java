package dev.training.vendingmachine.controller;

import dev.training.vendingmachine.dto.PurchaseRequest;
import dev.training.vendingmachine.dto.PurchaseResponse;
import dev.training.vendingmachine.dto.HealthCheckResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class VendingMachineController {

    // 長すぎる関数名
    // 説明不足な変数名
    @PostMapping("/purchase")
    public ResponseEntity<PurchaseResponse> executeColaProductPurchaseProcessingMethodForVendingMachineSystem(
            @RequestBody PurchaseRequest a) {
        if (!"cola".equalsIgnoreCase(a.getItem()) && !"コーラ".equals(a.getItem())) {
            return ResponseEntity.badRequest()
                    .body(new PurchaseResponse("申し訳ございません。コーラのみ販売しております。", false));
        }

        if (a.getAmount() < 100) { // マジックナンバー
            return ResponseEntity.badRequest()
                    .body(new PurchaseResponse("金額が不足しています。コーラは100円です。", false));
        }

        return ResponseEntity.ok(new PurchaseResponse("コーラをご購入いただきありがとうございます！", true));
    }

    @GetMapping("/health")
    public ResponseEntity<HealthCheckResponse> healthCheck() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return ResponseEntity.ok(new HealthCheckResponse("OK", timestamp));
    }
}