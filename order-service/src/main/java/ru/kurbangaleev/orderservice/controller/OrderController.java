package ru.kurbangaleev.orderservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kurbangaleev.orderservice.dto.OrderDto;
import ru.kurbangaleev.orderservice.enums.OrderStatus;
import ru.kurbangaleev.orderservice.model.Order;
import ru.kurbangaleev.orderservice.service.OrderService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody @Valid OrderDto orderDto) {
        OrderDto createdOrder = orderService.createOrder(orderDto);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        OrderDto order = orderService.getOrder(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getUserOrders(@PathVariable String userId) {
        List<OrderDto> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status);
        OrderDto updatedOrder = orderService.updateOrderStatus(id, orderStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> processPayment(@PathVariable Long id) {
        orderService.processPayment(id);
        return ResponseEntity.accepted().build();
    }
}