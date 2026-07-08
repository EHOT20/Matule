package com.example.matule.presentation.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.matule.R
import com.example.matule.data.repositories.CartRepository
import com.example.matule.data.repositories.MockProductRepository
import com.example.matule.databinding.FragmentCartBinding
import com.example.matule.domain.models.Product

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartRepository: CartRepository
    private lateinit var adapter: CartAdapter
    private val cartProductIds = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartRepository = CartRepository(requireContext())

        adapter = CartAdapter(cartProductIds) { productId ->
            cartRepository.removeProduct(productId)
            loadCart()
        }

        binding.cartRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.cartRecyclerView.adapter = adapter

        loadCart()

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val productId = cartProductIds[position]
                    if (direction == ItemTouchHelper.LEFT) {
                        cartRepository.removeProduct(productId)
                        loadCart()
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        Toast.makeText(requireContext(), "Изменить количество", Toast.LENGTH_SHORT).show()
                        adapter.notifyItemChanged(position)
                    }
                }
            }
        })
        touchHelper.attachToRecyclerView(binding.cartRecyclerView)

        binding.btnCheckout.setOnClickListener {
            if (cartProductIds.isNotEmpty()) {
                findNavController().navigate(R.id.action_cart_to_checkout)
            } else {
                Toast.makeText(requireContext(), "Корзина пуста", Toast.LENGTH_SHORT).show()
            }
        }

        updateTotal()
    }

    private fun loadCart() {
        cartProductIds.clear()
        cartProductIds.addAll(cartRepository.getCart())
        adapter.notifyDataSetChanged()
        updateTotal()
        binding.btnCheckout.isEnabled = cartProductIds.isNotEmpty()
    }

    private fun updateTotal() {
        val total = cartProductIds.sumOf { productId ->
            MockProductRepository.getProducts().firstOrNull { it.id == productId }?.price ?: 0.0
        }
        binding.totalPrice.text = "Итого: ${String.format("%.2f", total)} ₽"
    }

    inner class CartAdapter(
        private val items: List<String>,
        private val onRemove: (String) -> Unit
    ) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val productId = items[position]
            val product = MockProductRepository.getProducts().firstOrNull { it.id == productId }
            holder.bind(product, onRemove)
        }

        override fun getItemCount() = items.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val title: TextView = view.findViewById(R.id.productTitle)
            private val price: TextView = view.findViewById(R.id.productPrice)
            private val quantity: TextView = view.findViewById(R.id.productQuantity)
            private val imageView: ImageView = view.findViewById(R.id.productImage)
            private val removeButton: View = view.findViewById(R.id.removeButton)

            fun bind(product: Product?, onRemove: (String) -> Unit) {
                title.text = product?.title ?: "Неизвестный товар"
                price.text = "${product?.price ?: 0} ₽"
                quantity.text = "Кол-во: 1"

                // Загрузка картинки через Coil
                imageView.load(product?.imageUrl) {
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                }

                removeButton.setOnClickListener {
                    product?.id?.let { onRemove(it) }
                }
            }
        }
    }
}