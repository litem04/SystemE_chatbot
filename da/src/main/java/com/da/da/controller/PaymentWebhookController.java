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
    private final String configuredSecret;

    public PaymentWebhookController(PaymentWebhookService webhookService) {
        this(webhookService, "dev_secret_key_change_me_in_prod");
    }

    @org.springframework.beans.factory.annotation.Autowired
    public PaymentWebhookController(PaymentWebhookService webhookService,
                                    @Value("${payment.webhook.secret:dev_secret_key_change_me_in_prod}") String configuredSecret) {
        this.webhookService = webhookService;
        this.configuredSecret = configuredSecret;
    }

    @PostMapping("/vietqr")
    public ResponseEntity<?> handleVietQR(@Valid @RequestBody PaymentWebhookRequest payload,
                                          @RequestHeader(value = "X-Webhook-Secret", required = false) String requestSecret) {
        PaymentWebhookService.WebhookProcessResult result =
                webhookService.processVietQrWebhook(requestSecret, configuredSecret, payload);
        return ResponseEntity.status(result.httpStatus()).body(result.toResponseMap());
    }
}

