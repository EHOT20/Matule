package com.example.matule.presentation.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.matule.R

class PopularFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_popular, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Находим кнопку-сердечко и вешаем переход в избранное
        view.findViewById<View>(R.id.favoriteButton)?.setOnClickListener {
            findNavController().navigate(R.id.action_popular_to_favorites)
        }
    }
}