package ru.kurbangaleev.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String brand;
    private String model;
    private BigDecimal price;
    private String description;
    private Map<String, String> characteristics;
    private boolean available;
}
