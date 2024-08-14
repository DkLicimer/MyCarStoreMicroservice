package ru.kurbangaleev.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kurbangaleev.orderservice.dto.PaymentDto;
import ru.kurbangaleev.orderservice.dto.PaymentResponseDto;

import java.math.BigDecimal;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {
    @PostMapping("/api/payments")
    PaymentResponseDto processPayment(@RequestBody PaymentDto paymentDto);
}
