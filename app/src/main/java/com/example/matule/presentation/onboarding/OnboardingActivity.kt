package com.example.matule.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.matule.R
import com.example.matule.presentation.auth.LoginActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnAction: Button
    private lateinit var layoutIndicators: LinearLayout

    private val adapter by lazy { OnboardingPagerAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        btnAction = findViewById(R.id.btnAction)
        layoutIndicators = findViewById(R.id.layoutIndicators)

        viewPager.adapter = adapter
        setupIndicators()
        updateIndicators(0)
        updateButtonText(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
                updateButtonText(position)
            }
        })

        btnAction.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < adapter.itemCount - 1) {
                viewPager.currentItem = currentItem + 1
            } else {
                // Сохраняем, что онбординг показан
                val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                prefs.edit().putBoolean("onboarding_shown", true).apply()
                // Переход на экран входа (LoginActivity)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        // Анимация проявления (Fade)
        viewPager.setPageTransformer { page, position ->
            page.alpha = 1 - Math.abs(position)
        }
    }

    private fun setupIndicators() {
        val size = dpToPx(24)
        for (i in 0 until adapter.itemCount) {
            val dot = View(this)
            dot.setBackgroundResource(if (i == 0) R.drawable.dot_selected else R.drawable.dot_unselected)
            val params = LinearLayout.LayoutParams(size, size).apply {
                marginStart = dpToPx(8)
                marginEnd = dpToPx(8)
            }
            dot.layoutParams = params
            layoutIndicators.addView(dot)
        }
    }

    private fun updateIndicators(position: Int) {
        for (i in 0 until layoutIndicators.childCount) {
            val dot = layoutIndicators.getChildAt(i)
            dot.setBackgroundResource(if (i == position) R.drawable.dot_selected else R.drawable.dot_unselected)
        }
    }

    private fun updateButtonText(position: Int) {
        btnAction.text = if (position == adapter.itemCount - 1) "Начать" else "Далее"
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}