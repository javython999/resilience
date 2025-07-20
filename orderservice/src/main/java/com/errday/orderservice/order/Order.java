package com.errday.orderservice.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private int quantity;

    private LocalDateTime orderDate;

    public Order(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
        this.orderDate = LocalDateTime.now();
    }
}
