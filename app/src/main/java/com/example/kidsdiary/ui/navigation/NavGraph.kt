package com.example.kidsdiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.kidsdiary.ui.child.ChildDetailScreen
import com.example.kidsdiary.ui.child.ChildEditScreen
import com.example.kidsdiary.ui.growth.GrowthInputScreen
import com.example.kidsdiary.ui.home.HomeScreen
import com.example.kidsdiary.ui.photo.PhotoAddScreen
import com.example.kidsdiary.ui.photo.PhotoAlbumScreen

/** アプリ内のナビゲーションルート定義 */
sealed class Screen(val route: String) {
    /** ホーム画面（子供一覧） */
    data object Home : Screen("home")

    /** 子供登録画面（新規） */
    data object AddChild : Screen("child/add")

    /** 子供編集画面 */
    data object EditChild : Screen("child/edit/{childId}") {
        fun createRoute(childId: Long) = "child/edit/$childId"
    }

    /** 子供詳細画面 */
    data object ChildDetail : Screen("child/detail/{childId}") {
        fun createRoute(childId: Long) = "child/detail/$childId"
    }

    /** 成長記録入力画面 */
    data object AddGrowthRecord : Screen("growth/add/{childId}") {
        fun createRoute(childId: Long) = "growth/add/$childId"
    }

    /** 写真アルバム画面 */
    data object PhotoAlbum : Screen("photo/album/{childId}") {
        fun createRoute(childId: Long) = "photo/album/$childId"
    }

    /** 写真追加画面 */
    data object AddPhoto : Screen("photo/add/{childId}") {
        fun createRoute(childId: Long) = "photo/add/$childId"
    }
}

/**
 * アプリのナビゲーショングラフ
 */
@Composable
fun KidsDiaryNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // ホーム画面
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToChildDetail = { childId ->
                    navController.navigate(Screen.ChildDetail.createRoute(childId))
                },
                onNavigateToAddChild = {
                    navController.navigate(Screen.AddChild.route)
                }
            )
        }

        // 子供登録画面
        composable(Screen.AddChild.route) {
            ChildEditScreen(
                childId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 子供編集画面
        composable(
            route = Screen.EditChild.route,
            arguments = listOf(navArgument("childId") { type = NavType.LongType })
        ) { backStackEntry ->
            val childId = backStackEntry.arguments?.getLong("childId") ?: return@composable
            ChildEditScreen(
                childId = childId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 子供詳細画面
        composable(
            route = Screen.ChildDetail.route,
            arguments = listOf(navArgument("childId") { type = NavType.LongType })
        ) { backStackEntry ->
            val childId = backStackEntry.arguments?.getLong("childId") ?: return@composable
            ChildDetailScreen(
                childId = childId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditChild = { id ->
                    navController.navigate(Screen.EditChild.createRoute(id))
                },
                onNavigateToAddGrowthRecord = { id ->
                    navController.navigate(Screen.AddGrowthRecord.createRoute(id))
                },
                onNavigateToPhotoAlbum = { id ->
                    navController.navigate(Screen.PhotoAlbum.createRoute(id))
                }
            )
        }

        // 成長記録入力画面
        composable(
            route = Screen.AddGrowthRecord.route,
            arguments = listOf(navArgument("childId") { type = NavType.LongType })
        ) { backStackEntry ->
            val childId = backStackEntry.arguments?.getLong("childId") ?: return@composable
            GrowthInputScreen(
                childId = childId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 写真アルバム画面
        composable(
            route = Screen.PhotoAlbum.route,
            arguments = listOf(navArgument("childId") { type = NavType.LongType })
        ) { backStackEntry ->
            val childId = backStackEntry.arguments?.getLong("childId") ?: return@composable
            PhotoAlbumScreen(
                childId = childId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddPhoto = { id ->
                    navController.navigate(Screen.AddPhoto.createRoute(id))
                }
            )
        }

        // 写真追加画面
        composable(
            route = Screen.AddPhoto.route,
            arguments = listOf(navArgument("childId") { type = NavType.LongType })
        ) { backStackEntry ->
            val childId = backStackEntry.arguments?.getLong("childId") ?: return@composable
            PhotoAddScreen(
                childId = childId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
