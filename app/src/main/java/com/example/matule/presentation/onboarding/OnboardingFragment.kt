package com.example.matule.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.matule.R

class OnboardingFragment : Fragment() {

    companion object {
        private const val ARG_POSITION = "position"

        fun newInstance(position: Int): OnboardingFragment {
            return OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POSITION, position)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val position = arguments?.getInt(ARG_POSITION) ?: 0

        // Задаём содержимое в зависимости от позиции
        val title = view.findViewById<TextView>(R.id.textTitle)
        val description = view.findViewById<TextView>(R.id.textDescription)
        // Можно также менять изображение

        val titles = listOf("Добро пожаловать", "Выбирайте товары", "Оформляйте заказы")
        val descriptions = listOf(
            "Matule – ваш помощник в покупках",
            "Удобный каталог и поиск",
            "Быстрая доставка и оплата"
        )

        title.text = titles.getOrElse(position) { "Заголовок" }
        description.text = descriptions.getOrElse(position) { "Описание" }
    }
}