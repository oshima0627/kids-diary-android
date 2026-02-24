package com.example.kidsdiary.ui.photo

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kidsdiary.data.model.Photo
import com.example.kidsdiary.viewmodel.PhotoViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 写真追加画面
 * カメラ撮影またはギャラリーから写真を追加する
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoAddScreen(
    childId: Long,
    onNavigateBack: () -> Unit,
    photoViewModel: PhotoViewModel = viewModel()
) {
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var date by remember { mutableStateOf(System.currentTimeMillis()) }
    var comment by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showSourceDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date
    )

    // ギャラリーから画像を選択するランチャー
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            photoUri = it
            errorMessage = null
        }
    }

    // カメラで撮影するランチャー
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri = tempCameraUri
            errorMessage = null
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { date = it }
                    showDatePicker = false
                }) {
                    Text("決定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("キャンセル")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 写真ソース選択ダイアログ
    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("写真の取得元") },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text("カメラで撮影") },
                        leadingContent = {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                        },
                        modifier = Modifier.clickable {
                            showSourceDialog = false
                            val uri = createTempImageUri(context)
                            tempCameraUri = uri
                            cameraLauncher.launch(uri)
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("ギャラリーから選択") },
                        leadingContent = {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        },
                        modifier = Modifier.clickable {
                            showSourceDialog = false
                            galleryLauncher.launch("image/*")
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSourceDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("写真を追加") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 写真プレビュー
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clickable { showSourceDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "選択した写真",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "写真を追加",
                                    modifier = Modifier.size(60.dp),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "タップして写真を追加",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // 日付選択
            OutlinedTextField(
                value = formatDate(date),
                onValueChange = {},
                label = { Text("撮影日") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "日付を選択")
                    }
                }
            )

            // コメント入力
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("コメント（任意）") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                placeholder = { Text("この日の思い出を記録しましょう") }
            )

            // エラーメッセージ
            errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // 保存ボタン
            Button(
                onClick = {
                    if (photoUri == null) {
                        errorMessage = "写真を選択してください"
                        return@Button
                    }
                    val photo = Photo(
                        childId = childId,
                        date = date,
                        uri = photoUri.toString(),
                        comment = comment.trim().ifBlank { null }
                    )
                    photoViewModel.insertPhoto(photo)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = photoUri != null
            ) {
                Text("保存する")
            }
        }
    }
}

/** カメラ撮影用の一時ファイルURIを生成する */
private fun createTempImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.JAPAN).format(Date())
    val imageFile = File.createTempFile(
        "IMG_${timeStamp}_",
        ".jpg",
        File(context.cacheDir, "images").also { it.mkdirs() }
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )
}

private fun formatDate(epochMillis: Long): String {
    val sdf = SimpleDateFormat("yyyy年M月d日", Locale.JAPAN)
    return sdf.format(Date(epochMillis))
}
