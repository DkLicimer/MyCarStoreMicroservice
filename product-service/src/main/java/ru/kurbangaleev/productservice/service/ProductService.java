package ru.kurbangaleev.productservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kurbangaleev.productservice.client.OrderServiceClient;
import ru.kurbangaleev.productservice.dto.OrderDto;
import ru.kurbangaleev.productservice.dto.ProductDto;
import ru.kurbangaleev.productservice.model.Product;
import ru.kurbangaleev.productservice.repository.ProductRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final OrderServiceClient orderServiceClient;

    @Autowired
    public ProductService(ProductRepository productRepository, OrderServiceClient orderServiceClient) {
        this.productRepository = productRepository;
        this.orderServiceClient = orderServiceClient;
    }

    public ProductDto addProduct(ProductDto productDto) {
        Product product = convertToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return convertToDto(product);
    }

    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        updateProductFields(existingProduct, productDto);
        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDto(updatedProduct);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public void initiateOrder(Long productId, String userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (!product.isAvailable()) {
            throw new IllegalStateException("Product is not available for order");
        }

        OrderDto orderDto = new OrderDto();
        orderDto.setProductId(productId);
        orderDto.setUserId(userId);
        orderDto.setQuantity(1);

        orderServiceClient.createOrder(orderDto);
    }

    private Product convertToEntity(ProductDto productDto) {
        Product product = new Product();
        product.setId(productDto.getId());
        product.setBrand(productDto.getBrand());
        product.setModel(productDto.getModel());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCharacteristics(new HashMap<>(productDto.getCharacteristics()));
        product.setAvailable(productDto.isAvailable());
        return product;
    }

    private ProductDto convertToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setBrand(product.getBrand());
        productDto.setModel(product.getModel());
        productDto.setPrice(product.getPrice());
        productDto.setDescription(product.getDescription());
        productDto.setCharacteristics(new HashMap<>(product.getCharacteristics()));
        productDto.setAvailable(product.isAvailable());
        return productDto;
    }

    private void updateProductFields(Product existingProduct, ProductDto productDto) {
        if (productDto.getBrand() != null) {
            existingProduct.setBrand(productDto.getBrand());
        }
        if (productDto.getModel() != null) {
            existingProduct.setModel(productDto.getModel());
        }
        if (productDto.getPrice() != null) {
            existingProduct.setPrice(productDto.getPrice());
        }
        if (productDto.getDescription() != null) {
            existingProduct.setDescription(productDto.getDescription());
        }
        if (productDto.getCharacteristics() != null) {
            existingProduct.setCharacteristics(new HashMap<>(productDto.getCharacteristics()));
        }
        existingProduct.setAvailable(productDto.isAvailable());

        if (existingProduct.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        existingProduct.getCharacteristics().entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty());
    }
}
