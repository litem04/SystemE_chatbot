package com.da.da.controller;

import com.da.da.dto.PaymentWebhookRequest;
import com.da.da.service.PaymentWebhookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/webhook")
public class PaymentWebhookController {

    private final PaymentWebhookService webhookService;

    @Value("${payment.webhook.secret}")
    private String configuredSecret;

    public PaymentWebhookController(PaymentWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/vietqr")
    public ResponseEntity<?> handleVietQR(@Valid @RequestBody PaymentWebhookRequest payload,
                                          @RequestHeader(value = "X-Webhook-Secret", required = false) String requestSecret) {
        PaymentWebhookService.WebhookProcessResult result =
                webhookService.processVietQrWebhook(requestSecret, configuredSecret, payload);
        return ResponseEntity.status(result.httpStatus()).body(result.toResponseMap());
    }
}

