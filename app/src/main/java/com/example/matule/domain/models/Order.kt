package com.example.matule.domain.models

import java.util.Date

data class Order(
    val id: String,
    val items: List<Product>,
    val totalPrice: Double,
    val orderDate: Date,
    val status: OrderStatus
)

enum class OrderStatus {
    PENDING, SHIPPED, DELIVERED, CANCELLED
}