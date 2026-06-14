package com.library.android.presentation.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Icons translated 1:1 from the SVG path data in `docs/designs/catalog.html` so the on-device
 * UI matches the design exactly. Drawn in black; recoloured at use-sites via `Icon(tint = …)`.
 */
object StacksIcons {

    private fun strokeIcon(name: String, pathData: String, strokeWidth: Float): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).addPath(
            pathData = addPathNodes(pathData),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = strokeWidth,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ).build()

    private fun fillIcon(name: String, pathData: String): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).addPath(
            pathData = addPathNodes(pathData),
            fill = SolidColor(Color.Black),
        ).build()

    // .search svg: circle(cx11 cy11 r7) + handle line, stroke-width 2
    val Search: ImageVector =
        strokeIcon("Search", "M4 11a7 7 0 1014 0 7 7 0 10-14 0z M21 21l-4-4", strokeWidth = 2f)

    // .shelf-tab svg: location pin (fill)
    val Pin: ImageVector = fillIcon(
        "Pin",
        "M12 2C8.1 2 5 5.1 5 9c0 5.2 7 13 7 13s7-7.8 7-13c0-3.9-3.1-7-7-7zm0 9.5A2.5 " +
            "2.5 0 1112 6.5a2.5 2.5 0 010 5z",
    )

    // bottomnav icons (stroke-width 1.8)
    val NavCatalog: ImageVector =
        strokeIcon("NavCatalog", "M3 9.5L12 3l9 6.5V20a1 1 0 01-1 1H4a1 1 0 01-1-1z", 1.8f)
    val NavBorrowed: ImageVector =
        strokeIcon("NavBorrowed", "M4 5h16v14H4z M4 9h16", 1.8f)
    val NavChat: ImageVector =
        strokeIcon("NavChat", "M4 4h16v11H7l-3 3z", 1.8f)
    val NavProfile: ImageVector =
        strokeIcon("NavProfile", "M12 12a4 4 0 100-8 4 4 0 000 8z M5 20a7 7 0 0114 0", 1.8f)
}
