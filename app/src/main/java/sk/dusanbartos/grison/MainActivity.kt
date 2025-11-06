package sk.dusanbartos.grison

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import sk.dusanbartos.grison.ui.activation.ActivationRoute
import sk.dusanbartos.grison.ui.activation.ActivationViewModel
import sk.dusanbartos.grison.ui.main.MainRoute
import sk.dusanbartos.grison.ui.main.MainViewModel
import sk.dusanbartos.grison.ui.scratch.ScratchRoute
import sk.dusanbartos.grison.ui.scratch.ScratchViewModel
import sk.dusanbartos.grison.ui.theme.GrisonTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

@PreviewScreenSizes
@Composable
private fun App() {
    val navController = rememberNavController()
    val navActions = remember(navController) { NavActions(navController = navController) }

    GrisonTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = AppRoute.Main,
                enterTransition = { fadeIn(animationSpec = tween(0)) },
                exitTransition = { fadeOut(animationSpec = tween(0)) },
            ) {
                composable<AppRoute.Main> {
                    val viewModel: MainViewModel = hiltViewModel()
                    MainRoute(viewModel, navActions)
                }

                composable<AppRoute.Scratch> {
                    val viewModel: ScratchViewModel = hiltViewModel()
                    ScratchRoute(viewModel, navActions)
                }

                composable<AppRoute.Activation> {
                    val viewModel: ActivationViewModel = hiltViewModel()
                    ActivationRoute(viewModel, navActions)
                }
            }
        }
    }
}

