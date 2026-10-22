package dev.alnoer.audiosaas.feature.recordings

data class RecordingListItem(
    val title: String,
    val metadata: String,
    val status: String,
)

val SampleRecordings = listOf(
    RecordingListItem(
        title = "Интервью с пользователем",
        metadata = "48:06 · 2 спикера · сегодня",
        status = "ГОТОВО",
    ),
    RecordingListItem(
        title = "Weekly sync команды",
        metadata = "32:18 · 5 спикеров · вчера",
        status = "ГОТОВО",
    ),
    RecordingListItem(
        title = "Созвон с заказчиком",
        metadata = "21:42 · 3 спикера · 16 окт",
        status = "AI-СВОДКА",
    ),
    RecordingListItem(
        title = "Product research #12",
        metadata = "57:10 · 2 спикера · 14 окт",
        status = "ГОТОВО",
    ),
)

