package com.example.matule.presentation.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.matule.R
import com.example.matule.databinding.ActivityMainBinding
import com.example.matule.presentation.auth.LoginActivity
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: androidx.navigation.NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        drawerLayout = binding.drawerLayout

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_catalog,
                R.id.navigation_popular,
                R.id.navigation_favorites,
                R.id.navigation_cart,
                R.id.ordersFragment,
                R.id.profileFragment,
                R.id.notificationsFragment
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> navController.navigate(R.id.navigation_home)
                R.id.navigation_catalog -> navController.navigate(R.id.navigation_catalog)
                R.id.navigation_popular -> navController.navigate(R.id.navigation_popular)
                R.id.navigation_favorites -> navController.navigate(R.id.navigation_favorites)
                R.id.navigation_cart -> navController.navigate(R.id.navigation_cart)
                else -> return@setOnItemSelectedListener false
            }
            true
        }

        val navView: NavigationView = binding.navView
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> navController.navigate(R.id.profileFragment)
                R.id.nav_orders -> navController.navigate(R.id.ordersFragment)
                R.id.nav_favorites -> navController.navigate(R.id.navigation_favorites)
                R.id.nav_notifications -> navController.navigate(R.id.notificationsFragment)
                R.id.nav_logout -> {
                    // Очистка данных и выход
                    val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    prefs.edit().clear().apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}