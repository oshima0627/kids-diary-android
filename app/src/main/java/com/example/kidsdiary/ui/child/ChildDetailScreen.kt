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
import com.example.kidsdiary.data.GrowthStandard
import com.example.kidsdiary.data.model.Child
import com.example.kidsdiary.data.model.GrowthRecord
import com.example.kidsdiary.viewmodel.ChildViewModel
import com.example.kidsdiary.viewmodel.GrowthViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
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
    // remember(childId) で childId が変わったときだけ新しい Flow を生成し、
    // 無関係なリコンポーズで StateFlow が再生成されてデータが一瞬消えるのを防ぐ
    val child by remember(childId) { childViewModel.getChildById(childId) }.collectAsState()
    val records by remember(childId) { growthViewModel.getRecordsByChildId(childId) }.collectAsState()

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
                    records = records,
                    child = child
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
 * X軸は月齢（子供の誕生日からの経過月数）
 * 参考範囲（3〜97パーセンタイル）を点線で表示
 */
@Composable
private fun GrowthChart(
    records: List<GrowthRecord>,
    child: Child?
) {
    Column(modifier = Modifier.fillMaxSize()) {
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

        // 月齢を計算
        val ageMonthsList: List<Int> = if (child != null) {
            records.map { calculateAgeMonths(child.birthDate, it.date) }
        } else {
            records.indices.toList()
        }

        // 身長・体重エントリ（X軸 = 月齢）
        val heightEntries = records.mapIndexedNotNull { index, r ->
            r.heightCm?.let { Entry(ageMonthsList[index].toFloat(), it) }
        }
        val weightEntries = records.mapIndexedNotNull { index, r ->
            r.weightKg?.let { Entry(ageMonthsList[index].toFloat(), it) }
        }

        val minAge = ageMonthsList.minOrNull() ?: 0
        val maxAge = ageMonthsList.maxOrNull() ?: 0

        // 参考範囲エントリ（p3・p50・p97 を1ヶ月刻みで生成）
        val gender = child?.gender ?: "male"
        val heightRefEntries: Triple<List<Entry>, List<Entry>, List<Entry>> =
            buildRefEntries(minAge, maxAge) { m -> GrowthStandard.getHeightReference(gender, m) }
        val weightRefEntries: Triple<List<Entry>, List<Entry>, List<Entry>> =
            buildRefEntries(minAge, maxAge) { m -> GrowthStandard.getWeightReference(gender, m) }

        // X軸ラベル（月齢 → "Xヶ月" / "X歳Y" 表記）
        val xLabels: Map<Int, String> = ageMonthsList.mapIndexed { i, m ->
            m to formatAgeMonths(m)
        }.toMap()

        if (heightEntries.isNotEmpty()) {
            Text(
                "身長の推移 (cm)",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            AndroidView(
                factory = { context -> LineChart(context) },
                update = { chart ->
                    setupChart(
                        chart = chart,
                        entries = heightEntries,
                        refP3 = heightRefEntries.first,
                        refP50 = heightRefEntries.second,
                        refP97 = heightRefEntries.third,
                        xLabels = xLabels,
                        lineColor = heightColor.toArgb(),
                        label = "身長(cm)"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
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
                    setupChart(
                        chart = chart,
                        entries = weightEntries,
                        refP3 = weightRefEntries.first,
                        refP50 = weightRefEntries.second,
                        refP97 = weightRefEntries.third,
                        xLabels = xLabels,
                        lineColor = weightColor.toArgb(),
                        label = "体重(kg)"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }

        // 参考範囲の注釈
        if (child != null) {
            Text(
                text = "出典: こども家庭庁・文部科学省（0〜17歳 日本人小児発育標準値）",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * minAge〜maxAge の範囲で1ヶ月刻みに参考値エントリを生成する
 * @return Triple(p3エントリ, p50エントリ, p97エントリ)
 */
private fun buildRefEntries(
    minAge: Int,
    maxAge: Int,
    lookup: (Int) -> GrowthStandard.Percentile?
): Triple<List<Entry>, List<Entry>, List<Entry>> {
    val p3List = mutableListOf<Entry>()
    val p50List = mutableListOf<Entry>()
    val p97List = mutableListOf<Entry>()
    for (m in minAge..maxAge) {
        val ref = lookup(m) ?: continue
        p3List.add(Entry(m.toFloat(), ref.p3))
        p50List.add(Entry(m.toFloat(), ref.p50))
        p97List.add(Entry(m.toFloat(), ref.p97))
    }
    return Triple(p3List, p50List, p97List)
}

/** MPAndroidChart の折れ線グラフを設定する（参考範囲付き） */
private fun setupChart(
    chart: LineChart,
    entries: List<Entry>,
    refP3: List<Entry>,
    refP50: List<Entry>,
    refP97: List<Entry>,
    xLabels: Map<Int, String>,
    lineColor: Int,
    label: String
) {
    val refGray = Color(0xFFAAAAAA).toArgb()
    val refMedianColor = Color(0xFF4CAF50).toArgb() // 緑: 50パーセンタイル（中央値）

    // 実測値データセット
    val dataSet = LineDataSet(entries, label).apply {
        color = lineColor
        setCircleColor(lineColor)
        lineWidth = 2.5f
        circleRadius = 4f
        setDrawValues(true)
        valueTextSize = 10f
        mode = LineDataSet.Mode.LINEAR
    }

    // 3パーセンタイル（正常範囲下限）
    val dsP3 = LineDataSet(refP3, "正常範囲下限").apply {
        color = refGray
        lineWidth = 1f
        setDrawCircles(false)
        setDrawValues(false)
        enableDashedLine(8f, 4f, 0f)
    }

    // 50パーセンタイル（中央値）
    val dsP50 = LineDataSet(refP50, "中央値").apply {
        color = refMedianColor
        lineWidth = 1f
        setDrawCircles(false)
        setDrawValues(false)
        enableDashedLine(12f, 4f, 0f)
    }

    // 97パーセンタイル（正常範囲上限）
    val dsP97 = LineDataSet(refP97, "正常範囲上限").apply {
        color = refGray
        lineWidth = 1f
        setDrawCircles(false)
        setDrawValues(false)
        enableDashedLine(8f, 4f, 0f)
    }

    val dataSets: MutableList<ILineDataSet> = mutableListOf()
    if (refP3.isNotEmpty()) {
        dataSets.add(dsP3)
        dataSets.add(dsP50)
        dataSets.add(dsP97)
    }
    dataSets.add(dataSet)

    chart.apply {
        data = LineData(dataSets)
        description.isEnabled = false
        legend.apply {
            isEnabled = true
            form = Legend.LegendForm.LINE
            textSize = 10f
        }
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val m = value.toInt()
                    return xLabels[m] ?: formatAgeMonths(m)
                }
            }
            labelRotationAngle = -30f
        }
        axisRight.isEnabled = false
        setTouchEnabled(true)
        isDragEnabled = true
        setScaleEnabled(true)
        invalidate()
    }
}

/**
 * 誕生日と記録日から月齢（月数）を計算する
 */
private fun calculateAgeMonths(birthDate: Long, recordDate: Long): Int {
    val birthCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    birthCal.timeInMillis = birthDate
    val recordCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    recordCal.timeInMillis = recordDate
    val years = recordCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
    val months = recordCal.get(Calendar.MONTH) - birthCal.get(Calendar.MONTH)
    return years * 12 + months
}

/**
 * 月齢を "Xヶ月" / "X歳" / "X歳Yヶ月" 形式に変換
 */
private fun formatAgeMonths(months: Int): String {
    if (months < 0) return "${months}m"
    val years = months / 12
    val rem = months % 12
    return when {
        years == 0 -> "${months}ヶ月"
        rem == 0   -> "${years}歳"
        else       -> "${years}歳${rem}m"
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
