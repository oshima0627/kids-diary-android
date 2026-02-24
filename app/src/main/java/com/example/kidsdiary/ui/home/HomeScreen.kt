package com.example.kidsdiary.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kidsdiary.data.model.Child
import com.example.kidsdiary.viewmodel.ChildViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * ホーム画面 - 登録済みの子供一覧を表示する
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToChildDetail: (Long) -> Unit,
    onNavigateToAddChild: () -> Unit,
    childViewModel: ChildViewModel = viewModel()
) {
    val children by childViewModel.children.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("キッズ日記") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddChild
            ) {
                Icon(Icons.Default.Add, contentDescription = "子供を追加")
            }
        }
    ) { paddingValues ->
        if (children.isEmpty()) {
            // 子供が登録されていない場合の表示
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChildCare,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "子供を登録してください",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "+ ボタンから追加できます",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(children) { child ->
                    ChildCard(
                        child = child,
                        onClick = { onNavigateToChildDetail(child.id) }
                    )
                }
            }
        }
    }
}

/**
 * 子供カード - ホーム画面の各子供を表示するカード
 */
@Composable
fun ChildCard(
    child: Child,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // アイコン写真
            if (child.iconUri != null) {
                AsyncImage(
                    model = child.iconUri,
                    contentDescription = "${child.name}のアイコン",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = child.name.take(1),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // 子供の情報
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = child.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "生年月日: ${formatDate(child.birthDate)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "年齢: ${calculateAge(child.birthDate)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/** 日付をフォーマットする */
private fun formatDate(epochMillis: Long): String {
    val sdf = SimpleDateFormat("yyyy年M月d日", Locale.JAPAN)
    return sdf.format(Date(epochMillis))
}

/** 年齢を計算して文字列で返す */
private fun calculateAge(birthDateMillis: Long): String {
    val birthCal = Calendar.getInstance().apply { timeInMillis = birthDateMillis }
    val now = Calendar.getInstance()
    var years = now.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
    var months = now.get(Calendar.MONTH) - birthCal.get(Calendar.MONTH)
    if (months < 0) {
        years--
        months += 12
    }
    return if (years > 0) "${years}歳${months}ヶ月" else "${months}ヶ月"
}
