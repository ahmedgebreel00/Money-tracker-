package com.example.moneytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.moneytracker.data.Settings
import com.example.moneytracker.ui.AddEditScreen
import com.example.moneytracker.ui.CategoriesScreen
import com.example.moneytracker.ui.HomeScreen
import com.example.moneytracker.ui.MoneyViewModel
import com.example.moneytracker.ui.ReportsScreen
import com.example.moneytracker.ui.SettingsScreen
import com.example.moneytracker.ui.theme.MoneyTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MoneyApp() }
    }
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val Tabs = listOf(
    Tab("home", "Home", Icons.Rounded.Home),
    Tab("reports", "Reports", Icons.Rounded.PieChart),
    Tab("categories", "Categories", Icons.Rounded.Category),
    Tab("settings", "Settings", Icons.Rounded.Settings)
)

@Composable
private fun MoneyApp(vm: MoneyViewModel = viewModel()) {
    val settings by vm.settings.collectAsStateWithLifecycle()
    val current = settings

    // Wait for the saved settings so the app never flashes the wrong theme on launch.
    if (current == null) {
        Box(Modifier.fillMaxSize())
        return
    }

    MoneyTrackerTheme(
        themeId = current.themeId,
        darkMode = current.darkMode,
        dynamicColor = current.dynamicColor
    ) {
        AppNavigation(vm = vm, settings = current)
    }
}

@Composable
private fun AppNavigation(vm: MoneyViewModel, settings: Settings) {
    val nav = rememberNavController()
    val backStackEntry by nav.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (Tabs.any { it.route == route }) {
                NavigationBar {
                    Tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = route == tab.route,
                            onClick = {
                                nav.navigate(tab.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreen(
                    vm = vm,
                    currency = settings.currency,
                    onAdd = { nav.navigate("edit/0") },
                    onEdit = { id -> nav.navigate("edit/$id") }
                )
            }
            composable("reports") { ReportsScreen(vm = vm, currency = settings.currency) }
            composable("categories") { CategoriesScreen(vm = vm) }
            composable("settings") { SettingsScreen(vm = vm, settings = settings) }
            composable(
                route = "edit/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                AddEditScreen(
                    vm = vm,
                    id = entry.arguments?.getLong("id") ?: 0L,
                    currency = settings.currency,
                    onClose = { nav.popBackStack() }
                )
            }
        }
    }
}
