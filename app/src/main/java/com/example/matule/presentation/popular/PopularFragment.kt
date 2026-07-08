package com.example.matule.presentation.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.matule.R
import com.example.matule.data.repositories.FavoritesRepository
import com.example.matule.data.repositories.MockProductRepository
import com.example.matule.domain.models.Product

class PopularFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PopularAdapter
    private var products: List<Product> = emptyList()
    private lateinit var favoritesRepository: FavoritesRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_popular, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesRepository = FavoritesRepository(requireContext())

        recyclerView = view.findViewById(R.id.popularRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        products = MockProductRepository.getProducts()
        adapter = PopularAdapter(products) { product ->
            val bundle = Bundle().apply {
                putParcelableArrayList("products", ArrayList(products))
                putInt("position", products.indexOf(product))
            }
            findNavController().navigate(R.id.action_popular_to_details, bundle)
        }
        recyclerView.adapter = adapter
    }

    inner class PopularAdapter(
        private val items: List<Product>,
        private val onItemClick: (Product) -> Unit
    ) : RecyclerView.Adapter<PopularAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = items[position]
            holder.bind(product, onItemClick)
        }

        override fun getItemCount() = items.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val title: TextView = view.findViewById(R.id.productTitle)
            private val price: TextView = view.findViewById(R.id.productPrice)
            private val favoriteButton: ImageButton = view.findViewById(R.id.favoriteButton)
            private val productImage: ImageView = view.findViewById(R.id.productImage)

            fun bind(product: Product, onItemClick: (Product) -> Unit) {
                title.text = product.title
                price.text = "${product.price} ₽"

                // Загрузка картинки через Coil
                productImage.load(product.imageUrl) {
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                }

                val isFavorite = favoritesRepository.getFavorites().contains(product.id)
                favoriteButton.setImageResource(if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)

                favoriteButton.setOnClickListener {
                    val favoriteIds = favoritesRepository.getFavorites().toMutableList()
                    if (favoriteIds.contains(product.id)) {
                        favoriteIds.remove(product.id)
                        favoritesRepository.removeFavorite(product.id)
                        favoriteButton.setImageResource(R.drawable.ic_favorite_border)
                        Toast.makeText(it.context, "Удалено из избранного", Toast.LENGTH_SHORT).show()
                    } else {
                        favoriteIds.add(product.id)
                        favoritesRepository.addFavorite(product.id)
                        favoriteButton.setImageResource(R.drawable.ic_favorite)
                        Toast.makeText(it.context, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
                    }
                }

                itemView.setOnClickListener { onItemClick(product) }
            }
        }
    }
}