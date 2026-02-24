package com.example.kidsdiary.ui.child

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kidsdiary.data.model.GrowthRecord
import com.example.kidsdiary.viewmodel.ChildViewModel
import com.example.kidsdiary.viewmodel.GrowthViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * 子供詳細画面 - 成長記録一覧とグラフを表示する
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildDetailScreen(
    childId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEditChild: (Long) -> Unit,
    onNavigateToAddGrowthRecord: (Long) -> Unit,
    onNavigateToPhotoAlbum: (Long) -> Unit,
    childViewModel: ChildViewModel = viewModel(),
    growthViewModel: GrowthViewModel = viewModel()
) {
    val child by childViewModel.getChildById(childId).collectAsState()
    val records by growthViewModel.getRecordsByChildId(childId).collectAsState()
    val periodFilter by growthViewModel.periodFilter.collectAsState()
    val filteredRecords by growthViewModel.getFilteredRecords(childId).collectAsState(initial = emptyList())

    var showDeleteDialog by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf<GrowthRecord?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0: 一覧, 1: グラフ

    if (showDeleteDialog && recordToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("記録を削除") },
            text = { Text("この成長記録を削除しますか？") },
            confirmButton = {
                TextButton(onClick = {
                    recordToDelete?.let { growthViewModel.deleteRecord(it) }
                    showDeleteDialog = false
                    recordToDelete = null
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
                title = { Text(child?.name ?: "詳細") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToPhotoAlbum(childId) }) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "アルバム")
                    }
                    child?.let { c ->
                        IconButton(onClick = { onNavigateToEditChild(c.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "編集")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddGrowthRecord(childId) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "記録を追加")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // タブ切り替え
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("記録一覧") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("グラフ") }
                )
            }

            when (selectedTab) {
                0 -> GrowthRecordList(
                    records = records,
                    onDeleteRecord = { record ->
                        recordToDelete = record
                        showDeleteDialog = true
                    }
                )
                1 -> GrowthChart(
                    records = filteredRecords,
                    periodFilter = periodFilter,
                    onFilterChange = { growthViewModel.setPeriodFilter(it) }
                )
            }
        }
    }
}

/**
 * 成長記録一覧
 */
@Composable
private fun GrowthRecordList(
    records: List<GrowthRecord>,
    onDeleteRecord: (GrowthRecord) -> Unit
) {
    if (records.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.MonitorWeight,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "記録がまだありません",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(records) { record ->
                GrowthRecordItem(
                    record = record,
                    onDelete = { onDeleteRecord(record) }
                )
            }
        }
    }
}

/**
 * 成長記録の1行アイテム
 */
@Composable
private fun GrowthRecordItem(
    record: GrowthRecord,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatDate(record.date),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    record.heightCm?.let {
                        Text(
                            text = "身長 ${it}cm",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    record.weightKg?.let {
                        Text(
                            text = "体重 ${it}kg",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                record.note?.let { note ->
                    if (note.isNotBlank()) {
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "削除",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * 成長グラフ（MPAndroidChart を使用）
 */
@Composable
private fun GrowthChart(
    records: List<GrowthRecord>,
    periodFilter: GrowthViewModel.PeriodFilter,
    onFilterChange: (GrowthViewModel.PeriodFilter) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 期間フィルター
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GrowthViewModel.PeriodFilter.entries.forEach { filter ->
                FilterChip(
                    selected = periodFilter == filter,
                    onClick = { onFilterChange(filter) },
                    label = {
                        Text(
                            when (filter) {
                                GrowthViewModel.PeriodFilter.THREE_MONTHS -> "3ヶ月"
                                GrowthViewModel.PeriodFilter.SIX_MONTHS -> "6ヶ月"
                                GrowthViewModel.PeriodFilter.ONE_YEAR -> "1年"
                                GrowthViewModel.PeriodFilter.ALL -> "全期間"
                            }
                        )
                    }
                )
            }
        }

        if (records.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "グラフ表示にはデータが必要です",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }
            return
        }

        val heightColor = Color(0xFF6650A4)
        val weightColor = Color(0xFF7D5260)

        // 身長グラフ
        val heightEntries = records.mapIndexedNotNull { index, r ->
            r.heightCm?.let { Entry(index.toFloat(), it) }
        }
        // 体重グラフ
        val weightEntries = records.mapIndexedNotNull { index, r ->
            r.weightKg?.let { Entry(index.toFloat(), it) }
        }

        val dateLabels = records.map { formatDateShort(it.date) }

        if (heightEntries.isNotEmpty()) {
            Text(
                "身長の推移 (cm)",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            AndroidView(
                factory = { context -> LineChart(context) },
                update = { chart ->
                    setupChart(chart, heightEntries, dateLabels, heightColor.toArgb(), "身長(cm)")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        if (weightEntries.isNotEmpty()) {
            Text(
                "体重の推移 (kg)",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            AndroidView(
                factory = { context -> LineChart(context) },
                update = { chart ->
                    setupChart(chart, weightEntries, dateLabels, weightColor.toArgb(), "体重(kg)")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}

/** MPAndroidChart の折れ線グラフを設定する */
private fun setupChart(
    chart: LineChart,
    entries: List<Entry>,
    labels: List<String>,
    lineColor: Int,
    label: String
) {
    val dataSet = LineDataSet(entries, label).apply {
        color = lineColor
        setCircleColor(lineColor)
        lineWidth = 2f
        circleRadius = 4f
        setDrawValues(true)
        valueTextSize = 10f
    }

    chart.apply {
        data = LineData(dataSet)
        description.isEnabled = false
        legend.isEnabled = true
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return if (index >= 0 && index < labels.size) labels[index] else ""
                }
            }
            labelRotationAngle = -30f
        }
        axisRight.isEnabled = false
        invalidate()
    }
}

private fun formatDate(epochMillis: Long): String {
    val sdf = SimpleDateFormat("yyyy年M月d日", Locale.JAPAN)
    return sdf.format(Date(epochMillis))
}

private fun formatDateShort(epochMillis: Long): String {
    val sdf = SimpleDateFormat("M/d", Locale.JAPAN)
    return sdf.format(Date(epochMillis))
}
