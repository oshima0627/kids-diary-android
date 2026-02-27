package com.example.kidsdiary.data

/**
 * 日本人小児の身長・体重発育標準値
 *
 * データ出典：
 *  - 0〜6歳：こども家庭庁「令和5年乳幼児身体発育調査」（旧 厚生労働省 平成22年調査準拠）
 *  - 6〜17歳：文部科学省「令和5年度学校保健統計調査」
 *  - LMS法による平滑化：Isojima T et al. Clin Pediatr Endocrinol. 2016;25(2):71-76.
 *
 * 掲載パーセンタイル：3・50・97（概ね正常範囲の下限・中央・上限）
 * 定義点間は線形補間で月齢単位の値を算出します。
 */
object GrowthStandard {

    /** 3・50・97パーセンタイル値 */
    data class Percentile(val p3: Float, val p50: Float, val p97: Float)

    // ─── 男の子 身長 (cm) ────────────────────────────────────────
    // 0〜24ヶ月: 1ヶ月間隔, 24〜72ヶ月: 6ヶ月間隔, 72〜204ヶ月: 12ヶ月間隔
    private val boysHeightData = listOf(
        0   to Percentile(44.8f,  49.9f,  55.0f),
        1   to Percentile(50.3f,  55.6f,  61.0f),
        2   to Percentile(53.9f,  59.1f,  64.4f),
        3   to Percentile(56.7f,  61.8f,  67.0f),
        4   to Percentile(59.0f,  63.9f,  69.0f),
        5   to Percentile(60.8f,  65.7f,  70.7f),
        6   to Percentile(62.4f,  67.4f,  72.4f),
        7   to Percentile(63.8f,  69.0f,  74.2f),
        8   to Percentile(65.1f,  70.4f,  75.8f),
        9   to Percentile(66.4f,  71.8f,  77.3f),
        10  to Percentile(67.5f,  73.0f,  78.6f),
        11  to Percentile(68.6f,  74.2f,  79.8f),
        12  to Percentile(69.8f,  75.3f,  81.0f),
        13  to Percentile(70.7f,  76.3f,  82.1f),
        14  to Percentile(71.6f,  77.3f,  83.2f),
        15  to Percentile(72.5f,  78.3f,  84.2f),
        16  to Percentile(73.3f,  79.2f,  85.2f),
        17  to Percentile(74.1f,  80.1f,  86.2f),
        18  to Percentile(74.9f,  81.0f,  87.1f),
        19  to Percentile(75.7f,  81.8f,  88.0f),
        20  to Percentile(76.4f,  82.7f,  89.0f),
        21  to Percentile(77.2f,  83.5f,  89.9f),
        22  to Percentile(77.9f,  84.3f,  90.8f),
        23  to Percentile(78.6f,  85.1f,  91.7f),
        24  to Percentile(79.2f,  85.8f,  92.5f),
        30  to Percentile(82.0f,  88.8f,  95.7f),
        36  to Percentile(85.2f,  92.0f,  98.9f),
        42  to Percentile(88.1f,  95.1f, 102.3f),
        48  to Percentile(91.2f,  98.4f, 105.9f),
        54  to Percentile(94.1f, 101.6f, 109.4f),
        60  to Percentile(96.9f, 104.9f, 112.9f),
        66  to Percentile(99.8f, 108.0f, 116.3f),
        72  to Percentile(102.5f, 111.0f, 119.6f),
        84  to Percentile(107.5f, 117.5f, 127.5f),
        96  to Percentile(112.5f, 123.5f, 134.5f),
        108 to Percentile(117.5f, 129.5f, 141.5f),
        120 to Percentile(122.5f, 135.0f, 147.5f),
        132 to Percentile(128.0f, 141.0f, 154.0f),
        144 to Percentile(134.0f, 148.0f, 162.0f),
        156 to Percentile(141.0f, 156.0f, 171.0f),
        168 to Percentile(148.0f, 163.0f, 178.0f),
        180 to Percentile(153.0f, 167.5f, 182.0f),
        192 to Percentile(155.5f, 169.5f, 183.5f),
        204 to Percentile(157.0f, 170.7f, 184.4f)
    )

    // ─── 女の子 身長 (cm) ────────────────────────────────────────
    private val girlsHeightData = listOf(
        0   to Percentile(43.6f,  49.1f,  54.7f),
        1   to Percentile(48.7f,  54.0f,  59.4f),
        2   to Percentile(52.3f,  57.8f,  63.4f),
        3   to Percentile(55.0f,  60.7f,  66.4f),
        4   to Percentile(57.3f,  62.9f,  68.6f),
        5   to Percentile(59.2f,  64.8f,  70.4f),
        6   to Percentile(60.9f,  66.5f,  72.1f),
        7   to Percentile(62.3f,  68.0f,  73.7f),
        8   to Percentile(63.7f,  69.4f,  75.1f),
        9   to Percentile(65.0f,  70.8f,  76.6f),
        10  to Percentile(66.2f,  72.0f,  77.9f),
        11  to Percentile(67.4f,  73.3f,  79.1f),
        12  to Percentile(68.7f,  74.5f,  80.3f),
        13  to Percentile(69.7f,  75.6f,  81.5f),
        14  to Percentile(70.7f,  76.7f,  82.6f),
        15  to Percentile(71.6f,  77.7f,  83.7f),
        16  to Percentile(72.5f,  78.7f,  84.8f),
        17  to Percentile(73.3f,  79.6f,  85.9f),
        18  to Percentile(74.2f,  80.5f,  86.9f),
        19  to Percentile(75.0f,  81.3f,  87.7f),
        20  to Percentile(75.8f,  82.1f,  88.5f),
        21  to Percentile(76.5f,  82.9f,  89.3f),
        22  to Percentile(77.3f,  83.7f,  90.1f),
        23  to Percentile(78.0f,  84.5f,  90.9f),
        24  to Percentile(78.6f,  85.1f,  91.6f),
        30  to Percentile(81.3f,  87.9f,  94.6f),
        36  to Percentile(84.5f,  91.3f,  98.2f),
        42  to Percentile(87.6f,  94.6f, 101.8f),
        48  to Percentile(90.6f,  97.8f, 105.3f),
        54  to Percentile(93.5f, 101.0f, 108.7f),
        60  to Percentile(96.3f, 104.0f, 111.9f),
        66  to Percentile(99.1f, 107.0f, 115.0f),
        72  to Percentile(101.8f, 110.0f, 118.2f),
        84  to Percentile(107.0f, 116.5f, 126.0f),
        96  to Percentile(112.0f, 122.5f, 133.0f),
        108 to Percentile(117.5f, 129.5f, 141.5f),
        120 to Percentile(123.0f, 136.0f, 149.0f),
        132 to Percentile(129.5f, 143.0f, 156.5f),
        144 to Percentile(135.0f, 149.5f, 164.0f),
        156 to Percentile(140.0f, 154.5f, 169.0f),
        168 to Percentile(143.5f, 157.0f, 170.5f),
        180 to Percentile(145.5f, 158.0f, 170.5f),
        192 to Percentile(146.5f, 158.5f, 170.5f),
        204 to Percentile(147.0f, 158.0f, 169.0f)
    )

    // ─── 男の子 体重 (kg) ────────────────────────────────────────
    private val boysWeightData = listOf(
        0   to Percentile(2.29f,  3.04f,  3.88f),
        1   to Percentile(3.39f,  4.48f,  5.87f),
        2   to Percentile(4.24f,  5.66f,  7.48f),
        3   to Percentile(5.05f,  6.63f,  8.79f),
        4   to Percentile(5.69f,  7.44f,  9.88f),
        5   to Percentile(6.19f,  8.07f, 10.73f),
        6   to Percentile(6.59f,  8.61f, 11.46f),
        7   to Percentile(6.92f,  9.03f, 12.03f),
        8   to Percentile(7.20f,  9.39f, 12.54f),
        9   to Percentile(7.45f,  9.70f, 12.99f),
        10  to Percentile(7.69f, 10.00f, 13.38f),
        11  to Percentile(7.91f, 10.27f, 13.74f),
        12  to Percentile(8.14f, 10.54f, 14.10f),
        13  to Percentile(8.27f, 10.73f, 14.37f),
        14  to Percentile(8.41f, 10.93f, 14.63f),
        15  to Percentile(8.54f, 11.11f, 14.89f),
        16  to Percentile(8.67f, 11.29f, 15.15f),
        17  to Percentile(8.79f, 11.48f, 15.39f),
        18  to Percentile(8.92f, 11.66f, 15.62f),
        19  to Percentile(9.04f, 11.83f, 15.84f),
        20  to Percentile(9.16f, 12.00f, 16.07f),
        21  to Percentile(9.28f, 12.16f, 16.28f),
        22  to Percentile(9.41f, 12.33f, 16.49f),
        23  to Percentile(9.53f, 12.48f, 16.69f),
        24  to Percentile(9.65f, 12.63f, 16.87f),
        30  to Percentile(10.57f, 13.68f, 18.27f),
        36  to Percentile(11.28f, 14.56f, 19.41f),
        42  to Percentile(11.91f, 15.37f, 20.50f),
        48  to Percentile(12.52f, 16.18f, 21.64f),
        54  to Percentile(13.10f, 17.00f, 22.86f),
        60  to Percentile(13.72f, 17.86f, 24.17f),
        66  to Percentile(14.36f, 18.73f, 25.49f),
        72  to Percentile(14.97f, 19.59f, 26.81f),
        84  to Percentile(16.5f,  22.0f,  31.5f),
        96  to Percentile(18.5f,  25.0f,  37.5f),
        108 to Percentile(20.5f,  28.5f,  44.5f),
        120 to Percentile(23.0f,  32.5f,  52.0f),
        132 to Percentile(25.5f,  36.5f,  58.5f),
        144 to Percentile(29.0f,  41.5f,  65.0f),
        156 to Percentile(33.5f,  47.5f,  71.0f),
        168 to Percentile(39.5f,  53.5f,  76.0f),
        180 to Percentile(44.5f,  58.0f,  79.5f),
        192 to Percentile(48.0f,  61.0f,  81.5f),
        204 to Percentile(50.0f,  62.0f,  82.5f)
    )

    // ─── 女の子 体重 (kg) ────────────────────────────────────────
    private val girlsWeightData = listOf(
        0   to Percentile(2.21f,  2.96f,  3.72f),
        1   to Percentile(3.18f,  4.20f,  5.49f),
        2   to Percentile(3.97f,  5.30f,  6.99f),
        3   to Percentile(4.67f,  6.23f,  8.23f),
        4   to Percentile(5.24f,  6.94f,  9.13f),
        5   to Percentile(5.69f,  7.50f,  9.87f),
        6   to Percentile(6.05f,  7.97f, 10.48f),
        7   to Percentile(6.34f,  8.36f, 11.02f),
        8   to Percentile(6.60f,  8.69f, 11.52f),
        9   to Percentile(6.84f,  9.00f, 11.97f),
        10  to Percentile(7.06f,  9.27f, 12.38f),
        11  to Percentile(7.27f,  9.51f, 12.73f),
        12  to Percentile(7.47f,  9.75f, 13.07f),
        13  to Percentile(7.60f,  9.91f, 13.28f),
        14  to Percentile(7.74f, 10.08f, 13.50f),
        15  to Percentile(7.87f, 10.24f, 13.71f),
        16  to Percentile(8.00f, 10.40f, 13.91f),
        17  to Percentile(8.13f, 10.55f, 14.10f),
        18  to Percentile(8.25f, 10.70f, 14.28f),
        19  to Percentile(8.37f, 10.85f, 14.46f),
        20  to Percentile(8.49f, 11.00f, 14.64f),
        21  to Percentile(8.61f, 11.14f, 14.81f),
        22  to Percentile(8.74f, 11.29f, 14.99f),
        23  to Percentile(8.87f, 11.44f, 15.17f),
        24  to Percentile(9.00f, 11.58f, 15.35f),
        30  to Percentile(9.83f, 12.55f, 16.59f),
        36  to Percentile(10.56f, 13.39f, 17.76f),
        42  to Percentile(11.22f, 14.19f, 18.90f),
        48  to Percentile(11.85f, 15.00f, 20.09f),
        54  to Percentile(12.48f, 15.83f, 21.36f),
        60  to Percentile(13.08f, 16.68f, 22.66f),
        66  to Percentile(13.69f, 17.57f, 24.08f),
        72  to Percentile(14.29f, 18.45f, 25.55f),
        84  to Percentile(16.0f,  21.5f,  31.0f),
        96  to Percentile(17.5f,  25.0f,  38.0f),
        108 to Percentile(19.5f,  28.5f,  46.0f),
        120 to Percentile(22.0f,  33.0f,  55.0f),
        132 to Percentile(26.0f,  38.5f,  63.5f),
        144 to Percentile(31.0f,  44.5f,  70.0f),
        156 to Percentile(35.5f,  49.0f,  73.0f),
        168 to Percentile(38.5f,  51.5f,  73.5f),
        180 to Percentile(40.0f,  52.5f,  74.0f),
        192 to Percentile(40.5f,  52.5f,  74.0f),
        204 to Percentile(41.0f,  52.6f,  74.0f)
    )

    /**
     * 指定した性別・月齢の身長パーセンタイル値を返す（線形補間）
     * @param gender "male" / "female" / "other"（"other"は男の子データ使用）
     * @param ageMonths 月齢（0〜240）
     */
    fun getHeightReference(gender: String, ageMonths: Int): Percentile? {
        val data = if (gender == "female") girlsHeightData else boysHeightData
        return interpolate(data, ageMonths)
    }

    /**
     * 指定した性別・月齢の体重パーセンタイル値を返す（線形補間）
     */
    fun getWeightReference(gender: String, ageMonths: Int): Percentile? {
        val data = if (gender == "female") girlsWeightData else boysWeightData
        return interpolate(data, ageMonths)
    }

    /** 定義点間を線形補間して月齢 ageMonths の Percentile を返す */
    private fun interpolate(
        data: List<Pair<Int, Percentile>>,
        ageMonths: Int
    ): Percentile? {
        val sorted = data.sortedBy { it.first }
        if (ageMonths < sorted.first().first || ageMonths > sorted.last().first) return null

        val lower = sorted.lastOrNull { it.first <= ageMonths } ?: return null
        val upper = sorted.firstOrNull { it.first >= ageMonths } ?: return null

        if (lower.first == upper.first) return lower.second

        val ratio = (ageMonths - lower.first).toFloat() / (upper.first - lower.first)
        return Percentile(
            p3  = lower.second.p3  + ratio * (upper.second.p3  - lower.second.p3),
            p50 = lower.second.p50 + ratio * (upper.second.p50 - lower.second.p50),
            p97 = lower.second.p97 + ratio * (upper.second.p97 - lower.second.p97)
        )
    }
}
