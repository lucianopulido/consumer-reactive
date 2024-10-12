package com.luciano.springboot.ms.consumer.app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String orderId;
    private String customerId;
    private String customerName;
    private List<ProductDto> products;

    public Order(String orderId, String customerId, String customerName, List<ProductDto> products) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.products = products;
    }
}
