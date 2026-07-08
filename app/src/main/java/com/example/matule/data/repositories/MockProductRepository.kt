package com.example.matule.data.repositories

import com.example.matule.domain.models.Product

object MockProductRepository {
    fun getProducts(): List<Product> = listOf(
        Product("1", "Смартфон X", 599.99, "Отличный смартфон с большим экраном и мощным процессором. Поддерживает 5G, имеет камеру 108 Мп.", "https://via.placeholder.com/200"),
        Product("2", "Наушники Pro", 199.99, "Беспроводные наушники с шумоподавлением и длительным временем работы.", "https://via.placeholder.com/200"),
        Product("3", "Умные часы", 299.99, "Смарт-часы с GPS, мониторингом сна и пульса.", "https://via.placeholder.com/200"),
        Product("4", "Планшет", 399.99, "10-дюймовый планшет для работы и развлечений.", "https://via.placeholder.com/200")
    )
}