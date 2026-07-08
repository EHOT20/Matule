package com.example.matule.data.repositories

import com.example.matule.domain.models.Product

object MockProductRepository {
    fun getProducts(): List<Product> = listOf(
        Product(
            id = "1",
            title = "Смартфон X",
            price = 599.99,
            description = "Мощный смартфон с 6.7-дюймовым OLED-экраном, 108 Мп камерой и батареей 5000 мАч. Поддерживает 5G и быструю зарядку.",
            imageUrl = "https://picsum.photos/seed/phone1/200/200",
            isFavorite = false
        ),
        Product(
            id = "2",
            title = "Наушники Pro",
            price = 199.99,
            description = "Беспроводные наушники с активным шумоподавлением, поддержкой Hi-Res Audio и временем работы до 40 часов.",
            imageUrl = "https://picsum.photos/seed/headphones1/200/200",
            isFavorite = false
        ),
        Product(
            id = "3",
            title = "Умные часы",
            price = 299.99,
            description = "Стильные умные часы с AMOLED-дисплеем, встроенным GPS, мониторингом сердечного ритма и отслеживанием сна.",
            imageUrl = "https://picsum.photos/seed/watch1/200/200",
            isFavorite = false
        ),
        Product(
            id = "4",
            title = "Планшет Pro",
            price = 399.99,
            description = "10.5-дюймовый планшет с разрешением 2K, процессором восьмого поколения и поддержкой стилуса. Идеален для работы и творчества.",
            imageUrl = "https://picsum.photos/seed/tablet1/200/200",
            isFavorite = false
        ),
        Product(
            id = "5",
            title = "Беспроводная зарядка",
            price = 49.99,
            description = "Компактная беспроводная зарядная станция с поддержкой Qi, быстрая зарядка до 15 Вт.",
            imageUrl = "https://picsum.photos/seed/charger1/200/200",
            isFavorite = false
        )
    )
}