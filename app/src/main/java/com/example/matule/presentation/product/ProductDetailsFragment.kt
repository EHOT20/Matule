package com.example.matule.presentation.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.matule.R
import com.example.matule.data.repositories.CartRepository
import com.example.matule.data.repositories.FavoritesRepository
import com.example.matule.data.repositories.MockProductRepository
import com.example.matule.domain.models.Product

class ProductDetailsFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var titleTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var btnExpand: View
    private lateinit var btnAddToCart: View
    private lateinit var btnFavorite: ImageButton
    private lateinit var btnBack: ImageButton

    private var products: List<Product> = emptyList()
    private var currentPosition = 0
    private var isDescriptionExpanded = false
    private lateinit var favoritesRepository: FavoritesRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesRepository = FavoritesRepository(requireContext())

        products = arguments?.getParcelableArrayList<Product>("products") ?: MockProductRepository.getProducts()
        currentPosition = arguments?.getInt("position") ?: 0

        viewPager = view.findViewById(R.id.viewPager)
        titleTextView = view.findViewById(R.id.title)
        priceTextView = view.findViewById(R.id.price)
        descriptionTextView = view.findViewById(R.id.description)
        btnExpand = view.findViewById(R.id.btnExpandDescription)
        btnAddToCart = view.findViewById(R.id.btnAddToCart)
        btnFavorite = view.findViewById(R.id.btnFavorite)
        btnBack = view.findViewById(R.id.btnBack)

        viewPager.adapter = ProductImageAdapter(products)
        viewPager.currentItem = currentPosition
        updateProductDetails(currentPosition)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateProductDetails(position)
            }
        })

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        btnExpand.setOnClickListener {
            isDescriptionExpanded = !isDescriptionExpanded
            descriptionTextView.maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3
            btnExpand.isVisible = !isDescriptionExpanded
        }

        btnAddToCart.setOnClickListener {
            val product = products[currentPosition]
            val cartRepo = CartRepository(requireContext())
            cartRepo.addProduct(product.id)
            Toast.makeText(requireContext(), "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
        }

        btnFavorite.setOnClickListener {
            val product = products[currentPosition]
            val favoriteIds = favoritesRepository.getFavorites().toMutableList()
            if (favoriteIds.contains(product.id)) {
                favoriteIds.remove(product.id)
                favoritesRepository.removeFavorite(product.id)
                btnFavorite.setImageResource(R.drawable.ic_favorite_border)
                Toast.makeText(requireContext(), "Удалено из избранного", Toast.LENGTH_SHORT).show()
            } else {
                favoriteIds.add(product.id)
                favoritesRepository.addFavorite(product.id)
                btnFavorite.setImageResource(R.drawable.ic_favorite)
                Toast.makeText(requireContext(), "Добавлено в избранное", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProductDetails(position: Int) {
        val product = products[position]
        titleTextView.text = product.title
        priceTextView.text = "${product.price} ₽"
        descriptionTextView.text = product.description
        descriptionTextView.maxLines = 3
        isDescriptionExpanded = false
        btnExpand.isVisible = true
        val isFavorite = favoritesRepository.getFavorites().contains(product.id)
        btnFavorite.setImageResource(if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
    }

    inner class ProductImageAdapter(private val products: List<Product>) :
        androidx.viewpager2.adapter.FragmentStateAdapter(this@ProductDetailsFragment) {
        override fun getItemCount(): Int = products.size
        override fun createFragment(position: Int): Fragment {
            return ImageFragment.newInstance(products[position].imageUrl)
        }
    }

    class ImageFragment : Fragment() {
        companion object {
            fun newInstance(imageUrl: String) = ImageFragment().apply {
                arguments = Bundle().apply { putString("url", imageUrl) }
            }
        }
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val imageView = ImageView(requireContext())
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setImageResource(R.drawable.ic_launcher_foreground)
            return imageView
        }
    }
}