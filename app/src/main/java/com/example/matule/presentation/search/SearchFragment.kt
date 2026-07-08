package com.example.matule.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matule.R
import com.example.matule.data.repositories.MockProductRepository
import com.example.matule.domain.models.Product
import kotlinx.coroutines.*

class SearchFragment : Fragment() {

    private lateinit var searchInput: EditText
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var resultsRecyclerView: RecyclerView
    private val historyAdapter = HistoryAdapter()
    private val resultsAdapter = ResultsAdapter()

    private val searchHistory = mutableListOf<String>()
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchInput = view.findViewById(R.id.searchInput)
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)
        resultsRecyclerView = view.findViewById(R.id.resultsRecyclerView)

        historyRecyclerView.layoutManager = LinearLayoutManager(context)
        historyRecyclerView.adapter = historyAdapter

        resultsRecyclerView.layoutManager = LinearLayoutManager(context)
        resultsRecyclerView.adapter = resultsAdapter

        loadHistory()

        historyAdapter.onItemClick = { query ->
            searchInput.setText(query)
            performSearch(query)
        }

        searchInput.setOnEditorActionListener { _, _, _ ->
            val query = searchInput.text.toString().trim()
            if (query.isNotEmpty()) {
                performSearch(query)
            }
            true
        }
    }

    private fun performSearch(query: String) {
        scope.launch {
            if (!searchHistory.contains(query)) {
                searchHistory.add(0, query)
                saveHistory()
                historyAdapter.submitList(searchHistory)
            }
            val allProducts = MockProductRepository.getProducts()
            val results = allProducts.filter { product ->
                product.title.contains(query, ignoreCase = true)
            }
            resultsAdapter.submitList(results)
        }
    }

    private fun loadHistory() {
        val prefs = requireContext().getSharedPreferences("search", android.content.Context.MODE_PRIVATE)
        val history = prefs.getStringSet("history", emptySet())?.toMutableList() ?: mutableListOf()
        searchHistory.clear()
        searchHistory.addAll(history)
        historyAdapter.submitList(searchHistory)
    }

    private fun saveHistory() {
        val prefs = requireContext().getSharedPreferences("search", android.content.Context.MODE_PRIVATE)
        prefs.edit().putStringSet("history", searchHistory.toSet()).apply()
    }

    // Адаптер истории (внутренний класс)
    inner class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
        var onItemClick: ((String) -> Unit)? = null
        private var items: List<String> = emptyList()

        fun submitList(list: List<String>) {
            items = list
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = items[position]
            holder.itemView.setOnClickListener { onItemClick?.invoke(items[position]) }
        }

        override fun getItemCount() = items.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(android.R.id.text1)
        }
    }

    // Адаптер результатов (внутренний класс)
    inner class ResultsAdapter : RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {
        private var items: List<Product> = emptyList()

        fun submitList(list: List<Product>) {
            items = list
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = items[position]
            holder.title.text = product.title
            holder.price.text = "${product.price} ₽"
        }

        override fun getItemCount() = items.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.productTitle)
            val price: TextView = view.findViewById(R.id.productPrice)
        }
    }
}