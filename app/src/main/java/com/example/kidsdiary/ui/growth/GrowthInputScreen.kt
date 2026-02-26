package com.example.kidsdiary.ui.growth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kidsdiary.data.model.GrowthRecord
import com.example.kidsdiary.viewmodel.GrowthViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.TimeZone

/**
 * 身長・体重入力画面
 * 成長記録を新規登録する
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthInputScreen(
    childId: Long,
    onNavigateBack: () -> Unit,
    growthViewModel: GrowthViewModel = viewModel()
) {
    var date by remember {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        mutableStateOf(cal.timeInMillis)
    }
    var heightText by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date
    )

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("成長記録を入力") },
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
            // 日付選択
            OutlinedTextField(
                value = formatDate(date),
                onValueChange = {},
                label = { Text("記録日") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "日付を選択")
                    }
                }
            )

            // 身長入力
            OutlinedTextField(
                value = heightText,
                onValueChange = { heightText = it },
                label = { Text("身長 (cm)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                placeholder = { Text("例: 75.5") }
            )

            // 体重入力
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text("体重 (kg)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                placeholder = { Text("例: 9.8") }
            )

            // メモ
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("メモ（任意）") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            // エラーメッセージ
            errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 保存ボタン
            Button(
                onClick = {
                    val height = heightText.toFloatOrNull()
                    val weight = weightText.toFloatOrNull()

                    // バリデーション
                    when {
                        heightText.isBlank() && weightText.isBlank() -> {
                            errorMessage = "身長または体重のいずれかを入力してください"
                            return@Button
                        }
                        heightText.isNotBlank() && height == null -> {
                            errorMessage = "身長に正しい数値を入力してください"
                            return@Button
                        }
                        weightText.isNotBlank() && weight == null -> {
                            errorMessage = "体重に正しい数値を入力してください"
                            return@Button
                        }
                        height != null && (height < 0 || height > 300) -> {
                            errorMessage = "身長は0〜300cmの範囲で入力してください"
                            return@Button
                        }
                        weight != null && (weight < 0 || weight > 500) -> {
                            errorMessage = "体重は0〜500kgの範囲で入力してください"
                            return@Button
                        }
                    }

                    val record = GrowthRecord(
                        childId = childId,
                        date = date,
                        heightCm = height,
                        weightKg = weight,
                        note = note.trim().ifBlank { null }
                    )
                    growthViewModel.insertRecord(record)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("保存する")
            }
        }
    }
}

private fun formatDate(epochMillis: Long): String {
    val sdf = SimpleDateFormat("yyyy年M月d日", Locale.JAPAN)
    return sdf.format(Date(epochMillis))
}
