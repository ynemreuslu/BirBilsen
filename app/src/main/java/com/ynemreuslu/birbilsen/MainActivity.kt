package com.ynemreuslu.birbilsen

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.ynemreuslu.birbilsen.screen.network.NetworkControllerViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: NetworkControllerViewModel by viewModels { NetworkControllerViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            window.statusBarColor =
                getResources().getColor(R.color.md_theme_light_primary, getTheme())
            window.navigationBarColor =
                getResources().getColor(R.color.md_theme_light_primary, getTheme())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        // Check the network status when the activity is created
        viewModel.isNetworkAvailable.observe(this) { isConnected ->
            if (!isConnected) {
                val currentDestinationId = navController.currentDestination?.id
                if (currentDestinationId != R.id.networkControllerScreen) {
                    navController.navigate(R.id.networkControllerScreen)
                    navController.clearBackStack(R.id.nav_graph)
                }
            }

        }
    }
}









