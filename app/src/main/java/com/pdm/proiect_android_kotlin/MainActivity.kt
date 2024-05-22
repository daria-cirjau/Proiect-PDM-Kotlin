package com.pdm.proiect_android_kotlin

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pdm.proiect_android_kotlin.databinding.ActivityMain2Binding
import com.pdm.proiect_android_kotlin.service.ExerciseService
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMain2Binding

    companion object {
        lateinit var email: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_map, R.id.nav_add_exercises
            ), drawerLayout
        )

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_map -> {
                    val mapFragment = MapFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, mapFragment)
                        .addToBackStack(null)
                        .commit()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_add_exercises -> {
                    val addExercisesFragment = AddExercisesFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, addExercisesFragment)
                        .addToBackStack(null)
                        .commit()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }


        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        email = intent.getStringExtra("email") ?: "No Email"
        setNavigationHeaderEmail(email)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setNavigationHeaderEmail(email: String) {
        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val userEmailTextView = headerView.findViewById<TextView>(R.id.userEmail)
        userEmailTextView.text = email
    }

}