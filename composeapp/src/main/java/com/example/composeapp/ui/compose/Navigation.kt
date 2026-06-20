package com.example.composeapp.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.composeapp.data.enitities.suralist.SurahItem
import com.example.composeapp.viewmodel.SurahViewModel
import com.example.composeapp.viewmodel.ThemeViewModel
import kotlinx.serialization.Serializable

/**
 * @author i.m.mannapov
 */

@Serializable
object Main

@Serializable
data class SurahRoute(val index: String)

@Composable
fun MyNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    suraList: List<SurahItem>,
    themeViewModel: ThemeViewModel
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Main,
    ) {
        composable<Main> {
            Main(
                suraList = suraList,
                onSurahClick = { surah ->
                    surah.index?.let { index ->
                        navController.navigate(SurahRoute(index))
                    }
                },
                themeViewModel = themeViewModel
            )
        }
        composable<SurahRoute> { backStackEntry ->
            val route: SurahRoute = backStackEntry.toRoute()
            val surah = suraList.find { it.index == route.index }
            
            val surahViewModel: SurahViewModel = hiltViewModel()

            SurahScreen(
                surah = surah,
                allSuras = suraList,
                surahViewModel = surahViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
