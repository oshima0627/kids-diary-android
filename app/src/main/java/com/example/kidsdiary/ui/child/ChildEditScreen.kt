package com.example.kidsdiary.ui.child

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kidsdiary.data.model.Child
import com.example.kidsdiary.viewmodel.ChildViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * 子供登録・編集画面
 * childId が null の場合は新規登録、指定された場合は編集モード
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildEditScreen(
    childId: Long? = null,
    onNavigateBack: () -> Unit,
    childViewModel: ChildViewModel = viewModel()
) {
    val isEditMode = childId != null
    // collectAsState() は Composable のルール上、条件分岐の外で常に呼び出す必要がある
    // childId が null のとき -1L を渡すと Room からは null が返るため問題ない
    val existingChildState by childViewModel.getChildById(childId ?: -1L).collectAsState()
    val existingChild = if (isEditMode) existingChildState else null

    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<Long?>(null) }
    var gender by remember { mutableStateOf("male") }
    var iconUri by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    // 編集モードの場合は既存データをセット
    LaunchedEffect(existingChild) {
        existingChild?.let { child ->
            name = child.name
            birthDate = child.birthDate
            gender = child.gender
            iconUri = child.iconUri
        }
    }

    // 画像選択ランチャー
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { iconUri = it.toString() }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = birthDate ?: System.currentTimeMillis()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { birthDate = it }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "子供情報を編集" else "子供を追加") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // アイコン写真
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (iconUri != null) {
                    AsyncImage(
                        model = iconUri,
                        contentDescription = "子供のアイコン",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "写真を追加",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
            Text(
                text = "タップして写真を選択",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            // 名前入力
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("名前") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 生年月日
            OutlinedTextField(
                value = birthDate?.let { formatDate(it) } ?: "",
                onValueChange = {},
                label = { Text("生年月日") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "日付を選択")
                    }
                }
            )

            // 性別選択
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "性別",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("male" to "男の子", "female" to "女の子", "other" to "その他").forEach { (value, label) ->
                        FilterChip(
                            selected = gender == value,
                            onClick = { gender = value },
                            label = { Text(label) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 保存ボタン
            Button(
                onClick = {
                    if (name.isNotBlank() && birthDate != null) {
                        val child = Child(
                            id = existingChild?.id ?: 0L,
                            name = name.trim(),
                            birthDate = birthDate!!,
                            gender = gender,
                            iconUri = iconUri,
                            createdAt = existingChild?.createdAt ?: System.currentTimeMillis()
                        )
                        if (isEditMode) {
                            childViewModel.updateChild(child)
                        } else {
                            childViewModel.insertChild(child)
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && birthDate != null
            ) {
                Text(if (isEditMode) "更新する" else "登録する")
            }
        }
    }
}

private fun formatDate(epochMillis: Long): String {
    val sdf = SimpleDateFormat("yyyy年M月d日", Locale.JAPAN)
    return sdf.format(Date(epochMillis))
}
