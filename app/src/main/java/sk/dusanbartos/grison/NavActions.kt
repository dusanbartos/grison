package sk.dusanbartos.grison

import androidx.navigation.NavHostController

/**
 * Simple navigation wrapper
 * This can be transformed to a NavigationRepository if the navigation graph becomes more complex
 */
class NavActions(navController: NavHostController) {
    // [ASS-1]
    // for multi-card support, we would need to pass UUID via route params
    /*val navigateToScratch: (Card) -> Unit = { card ->
        navController.navigate(AppRoute.Scratch(card.uuid))
    }*/
    val navigateToScratch: () -> Unit = { navController.navigate(AppRoute.Scratch) }

    // [ASS-1]
    // for multi-card support, we would need to pass UUID via route params
    /*val navigateToActivation: (Card) -> Unit = { card ->
        navController.navigate(AppRoute.Activation(card.uuid))
    }*/
    val navigateToActivation: () -> Unit = { navController.navigate(AppRoute.Activation) }

    val goBack: () -> Unit = { navController.navigateUp() }
}