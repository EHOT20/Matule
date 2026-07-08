package com.example.matule.data.repositories

import com.example.matule.domain.models.Order
import com.example.matule.domain.models.OrderStatus
import com.example.matule.domain.models.Product
import java.util.Calendar
import java.util.Date

object MockOrderRepository {

    private fun getMockProducts(): List<Product> = listOf(
        Product("1", "Смартфон X", 599.99, "Отличный смартфон", "url", false),
        Product("2", "Наушники Pro", 199.99, "Беспроводные наушники", "url", false)
    )

    fun getOrders(): List<Order> {
        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -2)
        val twoDaysAgo = calendar.time

        return listOf(
            Order("1", getMockProducts(), 799.98, today, OrderStatus.PENDING),
            Order("2", getMockProducts().take(1), 599.99, yesterday, OrderStatus.SHIPPED),
            Order("3", getMockProducts().drop(1), 199.99, twoDaysAgo, OrderStatus.DELIVERED)
        )
    }
}