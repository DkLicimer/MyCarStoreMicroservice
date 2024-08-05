package ru.kurbangaleev.orderservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kurbangaleev.orderservice.model.Order;
import ru.kurbangaleev.orderservice.service.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestParam Long userId, @RequestParam Long productID) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(userId, productID));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @PostMapping("/{orderId}/payment")
    public ResponseEntity<Order> processPayment(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.processPayment(orderId));
    }
}
