package ru.kurbangaleev.paymentservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kurbangaleev.paymentservice.client.OrderServiceClient;
import ru.kurbangaleev.paymentservice.dto.PaymentDto;
import ru.kurbangaleev.paymentservice.dto.PaymentResponseDto;
import ru.kurbangaleev.paymentservice.enums.OrderStatus;
import ru.kurbangaleev.paymentservice.enums.PaymentStatus;
import ru.kurbangaleev.paymentservice.exception.ResourceNotFoundException;
import ru.kurbangaleev.paymentservice.model.Payment;
import ru.kurbangaleev.paymentservice.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderServiceClient orderServiceClient;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, OrderServiceClient orderServiceClient) {
        this.paymentRepository = paymentRepository;
        this.orderServiceClient = orderServiceClient;
    }

    public PaymentResponseDto processPayment(PaymentDto paymentDto) {

        Payment payment = new Payment();
        payment.setOrderId(paymentDto.getOrderId());
        payment.setAmount(paymentDto.getAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());

        boolean paymentSuccessful = processExternalPayment(paymentDto);

        if (paymentSuccessful) {
            payment.setStatus(PaymentStatus.SUCCESSFUL);
            payment.setTransactionId(generateTransactionId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        payment.setUpdatedAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        updateOrderStatus(payment.getOrderId(), payment.getStatus());

        return convertToResponseDto(savedPayment);
    }

    private boolean processExternalPayment(PaymentDto paymentDto) {
        return Math.random() < 0.95;
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    private void updateOrderStatus(Long orderId, PaymentStatus paymentStatus) {
        String orderStatus = paymentStatus == PaymentStatus.SUCCESSFUL
                ? OrderStatus.PAID.name()
                : OrderStatus.PENDING_PAYMENT.name();
        orderServiceClient.updateOrderStatus(orderId, orderStatus);
    }

    public PaymentDto getPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return convertToDto(payment);
    }

    public List<PaymentDto> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PaymentDto convertToDto(Payment payment) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setId(payment.getId());
        paymentDto.setOrderId(payment.getOrderId());
        paymentDto.setAmount(payment.getAmount());
        paymentDto.setStatus(payment.getStatus());
        paymentDto.setTransactionId(payment.getTransactionId());
        paymentDto.setCreatedAt(payment.getCreatedAt());
        paymentDto.setUpdatedAt(payment.getUpdatedAt());
        return paymentDto;
    }

    private PaymentResponseDto convertToResponseDto(Payment payment) {
        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setId(payment.getId());
        responseDto.setOrderId(payment.getOrderId());
        responseDto.setAmount(payment.getAmount());
        responseDto.setStatus(payment.getStatus());
        responseDto.setTransactionId(payment.getTransactionId());
        responseDto.setCreatedAt(payment.getCreatedAt());
        return responseDto;
    }
}
