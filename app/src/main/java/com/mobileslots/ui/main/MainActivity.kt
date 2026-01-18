package com.mobileslots.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.mobileslots.MobileSlotsApplication
import com.mobileslots.R
import com.mobileslots.data.repository.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewModel()
        setupToolbar()
        setupNavigation()
        initializeData()
    }

    private fun setupViewModel() {
        val database = (application as MobileSlotsApplication).database
        val userRepository = UserRepository(database.userDao(), database.userSettingsDao())
        val gameRepository = GameRepository(database.gameDao())
        val achievementRepository = AchievementRepository(
            database.achievementDao(),
            database.userAchievementDao()
        )

        val factory = MainViewModelFactory(userRepository, gameRepository, achievementRepository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    private fun setupNavigation() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.profileFragment,
                R.id.historyFragment,
                R.id.settingsFragment
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, findViewById(R.id.toolbar),
            R.string.app_name, R.string.app_name
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> navController.navigate(R.id.homeFragment)
                R.id.nav_slots -> navController.navigate(R.id.slotsFragment)
                R.id.nav_roulette -> navController.navigate(R.id.rouletteFragment)
                R.id.nav_blackjack -> navController.navigate(R.id.blackjackFragment)
                R.id.nav_profile -> navController.navigate(R.id.profileFragment)
                R.id.nav_history -> navController.navigate(R.id.historyFragment)
                R.id.nav_settings -> navController.navigate(R.id.settingsFragment)
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.currentUser.collect { user ->
                // Update navigation header with user info if needed
            }
        }

        lifecycleScope.launch {
            viewModel.error.collect { errorMessage ->
                errorMessage?.let {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        it,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun initializeData() {
        viewModel.initializeApp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchGames(newText ?: "")
                return true
            }
        })
        
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                navController.navigate(R.id.settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun openYouTubeTutorial() {
        val tutorialUrl = getString(R.string.tutorial_youtube_link)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tutorialUrl))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Unable to open YouTube",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}
