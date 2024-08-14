package ru.kurbangaleev.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kurbangaleev.paymentservice.enums.OrderStatus;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    @PutMapping("/api/orders/{id}/status")
    ResponseEntity<Void> updateOrderStatus(@PathVariable("id") Long id, @RequestParam OrderStatus status);
}
