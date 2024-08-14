package ru.kurbangaleev.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.kurbangaleev.productservice.dto.OrderDto;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    @PostMapping("/api/orders")
    ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto);
}
