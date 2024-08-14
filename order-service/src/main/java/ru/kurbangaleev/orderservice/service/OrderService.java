package ru.kurbangaleev.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import ru.kurbangaleev.orderservice.client.PaymentServiceClient;
import ru.kurbangaleev.orderservice.client.ProductServiceClient;
import ru.kurbangaleev.orderservice.dto.OrderDto;
import ru.kurbangaleev.orderservice.dto.PaymentDto;
import ru.kurbangaleev.orderservice.dto.PaymentResponseDto;
import ru.kurbangaleev.orderservice.dto.ProductDto;
import ru.kurbangaleev.orderservice.enums.OrderStatus;
import ru.kurbangaleev.orderservice.model.Order;
import ru.kurbangaleev.orderservice.model.OrderItem;
import ru.kurbangaleev.orderservice.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        ProductServiceClient productServiceClient,
                        PaymentServiceClient paymentServiceClient) {
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }

    public OrderDto createOrder(OrderDto orderDto) {
        ProductDto product = productServiceClient.getProduct(orderDto.getProductId());
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }

        Order order = new Order();
        order.setUserId(orderDto.getUserId());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        OrderItem item = new OrderItem();
        item.setProductId(product.getId());
        item.setQuantity(orderDto.getQuantity());
        item.setPrice(product.getPrice());
        item.setOrder(order);
        order.getItems().add(item);

        order.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(orderDto.getQuantity())));

        Order savedOrder = orderRepository.save(order);
        return convertToDto(savedOrder);
    }

    public OrderDto getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return convertToDto(order);
    }

    public List<OrderDto> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public OrderDto updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        return convertToDto(orderRepository.save(order));
    }

    public void processPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setOrderId(orderId);
        paymentDto.setAmount(order.getTotalAmount());

        PaymentResponseDto paymentResponse = paymentServiceClient.processPayment(paymentDto);

        if (paymentResponse.isSuccess()) {
            updateOrderStatus(orderId, OrderStatus.PAID);
        } else {
            updateOrderStatus(orderId, OrderStatus.PENDING_PAYMENT);
        }
    }

    private OrderDto convertToDto(Order order) {
        OrderDto orderDto = new OrderDto();

        orderDto.setId(order.getId());
        orderDto.setUserId(order.getUserId());
        if (!order.getItems().isEmpty()) {
            OrderItem firstItem = order.getItems().get(0);
            orderDto.setProductId(firstItem.getProductId());
            orderDto.setQuantity(firstItem.getQuantity());
        }
        orderDto.setStatus(order.getStatus());
        orderDto.setTotalAmount(order.getTotalAmount());
        orderDto.setCreatedAt(order.getCreatedAt());
        orderDto.setUpdatedAt(order.getUpdatedAt());

        return orderDto;
    }

}

