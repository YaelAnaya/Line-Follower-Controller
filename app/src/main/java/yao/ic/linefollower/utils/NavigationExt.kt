package yao.ic.linefollower.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut

inline fun <reified T : Any> NavGraphBuilder.composable(
    crossinline content: @Composable (NavBackStackEntry) -> Unit
) {
    composable<T>(
        enterTransition = {
            materialFadeThroughIn()
        },
        exitTransition = {
            materialFadeThroughOut()
        },
        popEnterTransition = {
            materialSharedAxisZIn(forward = true)
        },
        popExitTransition = {
            materialSharedAxisZOut(forward = false)
        }
    ) {
        content(it)
    }

}

