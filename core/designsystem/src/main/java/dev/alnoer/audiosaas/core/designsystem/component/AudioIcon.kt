package dev.alnoer.audiosaas.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import dev.alnoer.audiosaas.core.designsystem.R

enum class AudioIconAsset(@param:DrawableRes val resourceId: Int) {
    Add(R.drawable.ic_add),
    ArrowLeft(R.drawable.ic_arrow_left),
    Folder(R.drawable.ic_folder),
    History(R.drawable.ic_history),
    Overview(R.drawable.ic_overview),
    Play(R.drawable.ic_play),
    SaasMark(R.drawable.ic_saas_mark),
    Share(R.drawable.ic_share),
    SortDescending(R.drawable.ic_sort_desc),
}

@Composable
fun AudioIcon(
    asset: AudioIconAsset,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
) {
    Icon(
        painter = painterResource(asset.resourceId),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}


