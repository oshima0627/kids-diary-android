package com.example.kidsdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.kidsdiary.ui.navigation.KidsDiaryNavGraph
import com.example.kidsdiary.ui.theme.KidsDiaryTheme

/**
 * アプリのメインアクティビティ
 * Jetpack Compose と Navigation を使用してアプリ全体を管理する
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KidsDiaryTheme {
                val navController = rememberNavController()
                KidsDiaryNavGraph(navController = navController)
            }
        }
    }
}
