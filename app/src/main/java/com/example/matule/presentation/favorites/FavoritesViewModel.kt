package com.example.matule.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.matule.data.repositories.FavoritesRepository
import com.example.matule.data.repositories.MockProductRepository
import com.example.matule.domain.models.Product

class FavoritesViewModel(
    private val repository: FavoritesRepository
) : ViewModel() {

    private val _favorites = MutableLiveData<List<Product>>(emptyList())
    val favorites: LiveData<List<Product>> = _favorites

    fun loadFavorites() {
        val favoriteIds = repository.getFavorites()
        val allProducts = MockProductRepository.getProducts()
        val favoriteProducts = allProducts.filter { favoriteIds.contains(it.id) }
        _favorites.value = favoriteProducts
    }

    fun toggleFavorite(product: Product) {
        val favoriteIds = repository.getFavorites().toMutableList()
        if (favoriteIds.contains(product.id)) {
            favoriteIds.remove(product.id)
        } else {
            favoriteIds.add(product.id)
        }
        // Сохраняем обновлённый список
        repository.addFavorite(product.id) // временно, но лучше пересохранить весь список
        // Обновляем локальный список
        val current = _favorites.value ?: emptyList()
        val updated = if (favoriteIds.contains(product.id)) {
            current + product
        } else {
            current.filter { it.id != product.id }
        }
        _favorites.value = updated
    }
}

class FavoritesViewModelFactory(private val repository: FavoritesRepository) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}