package com.example.matule.presentation.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matule.R
import com.example.matule.data.repositories.MockOrderRepository
import com.example.matule.databinding.FragmentOrdersBinding
import com.example.matule.domain.models.Order
import java.text.SimpleDateFormat
import java.util.*

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var adapter: OrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orders = MockOrderRepository.getOrders()
        adapter = OrdersAdapter(orders)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val order = adapter.getOrderAt(position)
                    when (direction) {
                        ItemTouchHelper.RIGHT -> {
                            Toast.makeText(requireContext(), "Повтор заказа #${order.id}", Toast.LENGTH_SHORT).show()
                            adapter.notifyItemChanged(position)
                        }
                        ItemTouchHelper.LEFT -> {
                            Toast.makeText(requireContext(), "Заказ #${order.id} отменён", Toast.LENGTH_SHORT).show()
                            adapter.removeOrder(position)
                        }
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    inner class OrdersAdapter(private var orders: List<Order>) :
        RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

        fun getOrderAt(position: Int): Order = orders[position]

        fun removeOrder(position: Int) {
            orders = orders.toMutableList().apply { removeAt(position) }
            notifyItemRemoved(position)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(orders[position])
        }

        override fun getItemCount(): Int = orders.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val orderId: TextView = itemView.findViewById(R.id.orderId)
            private val orderDate: TextView = itemView.findViewById(R.id.orderDate)
            private val orderTotal: TextView = itemView.findViewById(R.id.orderTotal)
            private val orderStatus: TextView = itemView.findViewById(R.id.orderStatus)

            fun bind(order: Order) {
                orderId.text = "Заказ #${order.id}"
                orderTotal.text = "Итого: ${order.totalPrice} ₽"
                orderStatus.text = order.status.name
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                orderDate.text = dateFormat.format(order.orderDate)
            }
        }
    }
}