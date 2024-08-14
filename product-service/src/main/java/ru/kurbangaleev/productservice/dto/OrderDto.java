package ru.kurbangaleev.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kurbangaleev.productservice.enums.OrderStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private Long productId;
    private String userId;
    private int quantity;
    private OrderStatus status;
}
