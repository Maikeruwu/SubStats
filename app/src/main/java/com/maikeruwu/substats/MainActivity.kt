package com.maikeruwu.substats

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maikeruwu.substats.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Track both keyboard and destination state
    var isKeyboardVisible = true
    var isTopLevelDestination = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_statistics,
                R.id.navigation_manage,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Disable the bottom navigation menu when a keyboard is on screen
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.rootView.height
                val keypadHeight = screenHeight - rect.bottom
                isKeyboardVisible = keypadHeight <= screenHeight * 0.15
                updateBottomNavigationVisibility()
            }
        })

        // Disable the bottom navigation menu when a fragment is opened
        navController.addOnDestinationChangedListener { _, destination, _ ->
            isTopLevelDestination =
                appBarConfiguration.topLevelDestinations.contains(destination.id)
            updateBottomNavigationVisibility()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun updateBottomNavigationVisibility() {
        showBottomNavigation(isKeyboardVisible && isTopLevelDestination)
    }

    private fun showBottomNavigation(show: Boolean = true) {
        binding.navView.visibility = if (show) View.VISIBLE else View.INVISIBLE
        binding.navView.layoutParams.height = if (show) ViewGroup.LayoutParams.WRAP_CONTENT else 0
        binding.navView.requestLayout()
    }
}