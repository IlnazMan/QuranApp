package com.example.composeapp.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeapp.data.enitities.suralist.SurahItem
import kotlinx.serialization.Serializable

/**
 * @author i.m.mannapov
 */

@Serializable
object Main

@Serializable
object Surah

@Composable
fun MyNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    suraList: List<SurahItem>,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Main,
    ) {
        composable<Main> {
            Main(
                suraList = suraList,
                onSurahClick = { navController.navigate(Surah) },
            )
        }
        composable<Surah> {
            Surah()
        }
    }

}