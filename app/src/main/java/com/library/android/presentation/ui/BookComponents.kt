package com.library.android.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.library.android.domain.model.Book
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType

/**
 * Book UI primitives shared by the catalog list and the detail screen (.cover, .avail,
 * .shelf-tab from the Stacks design), so both render identically.
 */

/** CSS-drawn cover: coloured spine block + title (+ optional author), bottom-aligned (.cover). */
@Composable
fun BookCover(
    book: Book,
    modifier: Modifier = Modifier,
    titleSize: TextUnit = 11.sp,
    showAuthor: Boolean = false,
) {
    Box(modifier.clip(RoundedCornerShape(6.dp)).background(coverColor(book))) {
        Box(Modifier.fillMaxHeight().width(8.dp).background(Color.Black.copy(alpha = 0.12f)))
        Box(Modifier.align(Alignment.BottomStart).padding(12.dp)) {
            Column {
                Text(
                    text = book.title,
                    color = Color.White,
                    fontFamily = StacksType.Display,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = titleSize,
                    lineHeight = titleSize * 1.2f,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                if (showAuthor) {
                    Text(
                        text = book.author,
                        color = Color.White.copy(alpha = 0.85f),
                        fontFamily = StacksType.Body,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}

/** Availability pill (.avail.in / .avail.out). */
@Composable
fun AvailabilityChip(available: Boolean) {
    val foreground = if (available) StacksColors.Pine else StacksColors.Faint
    val background = if (available) StacksColors.Sage100 else StacksColors.OutChipBg
    val label = if (available) "Available" else "Borrowed"
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 9.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(foreground))
        Text(
            text = label,
            color = foreground,
            fontFamily = StacksType.Body,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
        )
    }
}

/** The signature shelf-location tab (.shelf-tab): brass left-bar + pin + "{shelf}·{row}". */
@Composable
fun ShelfTab(shelf: String, row: Int) {
    val shape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(StacksColors.Brass100)
            .border(1.dp, StacksColors.Brass.copy(alpha = 0.35f), shape),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.fillMaxHeight().width(3.dp).background(StacksColors.Brass))
        Row(
            modifier = Modifier.padding(start = 7.dp, top = 3.dp, end = 8.dp, bottom = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = StacksIcons.Pin,
                contentDescription = null,
                tint = StacksColors.Brass,
                modifier = Modifier.size(11.dp),
            )
            Row {
                Text(
                    text = shelf,
                    color = StacksColors.Brass,
                    fontFamily = StacksType.Mono,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                )
                Text(
                    text = "·$row",
                    color = StacksColors.Brass700,
                    fontFamily = StacksType.Mono,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

/** Deterministic spine colour per book from the design's cover palette. */
private fun coverColor(book: Book): Color {
    val palette = StacksColors.CoverPalette
    val index = ((book.id.hashCode() % palette.size) + palette.size) % palette.size
    return palette[index]
}
