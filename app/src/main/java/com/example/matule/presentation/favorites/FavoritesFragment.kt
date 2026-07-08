package com.example.matule.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matule.R
import com.example.matule.data.repositories.FavoritesRepository
import com.example.matule.data.repositories.MockProductRepository
import com.example.matule.domain.models.Product

class FavoritesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoritesAdapter
    private lateinit var viewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = FavoritesRepository(requireContext())
        viewModel = ViewModelProvider(this, FavoritesViewModelFactory(repository))
            .get(FavoritesViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = FavoritesAdapter { product ->
            viewModel.toggleFavorite(product)
            // Обновляем список после удаления
            viewModel.loadFavorites()
        }
        recyclerView.adapter = adapter

        viewModel.favorites.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }

        // При возврате на вкладку обновляем список
        viewModel.loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavorites()
    }

    class FavoritesAdapter(
        private val onFavoriteClick: (Product) -> Unit
    ) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

        private var items: List<Product> = emptyList()

        fun submitList(list: List<Product>) {
            items = list
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favorite, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = items[position]
            holder.bind(product, onFavoriteClick)
        }

        override fun getItemCount() = items.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val title: TextView = itemView.findViewById(R.id.productTitle)
            private val price: TextView = itemView.findViewById(R.id.productPrice)
            private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)

            fun bind(product: Product, onFavoriteClick: (Product) -> Unit) {
                title.text = product.title
                price.text = "${product.price} ₽"
                favoriteButton.setImageResource(R.drawable.ic_favorite)
                favoriteButton.setOnClickListener {
                    onFavoriteClick(product)
                }
            }
        }
    }
}