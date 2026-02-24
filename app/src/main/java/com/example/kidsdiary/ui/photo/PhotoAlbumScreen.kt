package com.example.kidsdiary.ui.photo

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kidsdiary.data.model.Photo
import com.example.kidsdiary.viewmodel.PhotoViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * 写真アルバム画面 - 月ごとの写真一覧を表示する
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoAlbumScreen(
    childId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToAddPhoto: (Long) -> Unit,
    photoViewModel: PhotoViewModel = viewModel()
) {
    val now = Calendar.getInstance()
    var currentYear by remember { mutableIntStateOf(now.get(Calendar.YEAR)) }
    var currentMonth by remember { mutableIntStateOf(now.get(Calendar.MONTH) + 1) }

    // remember で year/month が変わったときだけ新しい Flow を生成し、
    // 無関係なリコンポーズで StateFlow が再生成されてリストが一瞬消えるのを防ぐ
    val photos by remember(childId, currentYear, currentMonth) {
        photoViewModel.getPhotosByMonth(childId, currentYear, currentMonth)
    }.collectAsState()
    var selectedPhoto by remember { mutableStateOf<Photo?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog && selectedPhoto != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("写真を削除") },
            text = { Text("この写真を削除しますか？") },
            confirmButton = {
                TextButton(onClick = {
                    selectedPhoto?.let { photoViewModel.deletePhoto(it) }
                    showDeleteDialog = false
                    selectedPhoto = null
                }) {
                    Text("削除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("アルバム") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToAddPhoto(childId) }) {
                Icon(Icons.Default.Add, contentDescription = "写真を追加")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 年月ナビゲーション
            MonthNavigator(
                year = currentYear,
                month = currentMonth,
                onPrevMonth = {
                    if (currentMonth == 1) {
                        currentYear--
                        currentMonth = 12
                    } else {
                        currentMonth--
                    }
                },
                onNextMonth = {
                    if (currentMonth == 12) {
                        currentYear++
                        currentMonth = 1
                    } else {
                        currentMonth++
                    }
                }
            )

            if (photos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Photo,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "この月の写真はありません",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(photos) { photo ->
                        PhotoThumbnail(
                            photo = photo,
                            onLongClick = {
                                selectedPhoto = photo
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 月ナビゲーターコンポーネント
 */
@Composable
private fun MonthNavigator(
    year: Int,
    month: Int,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrevMonth) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "前の月")
        }
        Text(
            text = "${year}年${month}月",
            style = MaterialTheme.typography.titleMedium
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ChevronRight, contentDescription = "次の月")
        }
    }
}

/**
 * 写真サムネイルコンポーネント
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PhotoThumbnail(
    photo: Photo,
    onLongClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
    ) {
        AsyncImage(
            model = photo.uri,
            contentDescription = photo.comment ?: "写真",
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    onLongClick = onLongClick,
                    onClick = {}
                ),
            contentScale = ContentScale.Crop
        )
        // 日付オーバーレイ
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = formatDateShort(photo.date),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

private fun formatDateShort(epochMillis: Long): String {
    val sdf = SimpleDateFormat("M/d", Locale.JAPAN)
    return sdf.format(Date(epochMillis))
}
