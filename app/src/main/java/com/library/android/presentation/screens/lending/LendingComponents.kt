package com.library.android.presentation.screens.lending

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.library.android.domain.model.Loan
import com.library.android.domain.model.LoanStatus
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType

/** A loan card (.card): cover + title + status badge + due date, with role-aware actions. */
@Suppress("LongMethod") // declarative Compose card; splitting would fragment one cohesive layout
@Composable
fun LoanRow(
    loan: Loan,
    isStaff: Boolean,
    onReturn: (String) -> Unit,
    onApprove: (String) -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(StacksColors.Surface)
            .border(1.dp, StacksColors.Line, shape)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val title = "Book ${loan.bookId}"
        LoanCover(loan.bookId, title, Modifier.width(62.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = StacksColors.Ink,
                fontFamily = StacksType.Display,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LoanStatusChip(loan.status)
                Text(
                    text = "Due ${loan.dueAt.substringBefore('T')}",
                    color = StacksColors.Muted,
                    fontFamily = StacksType.Mono,
                    fontSize = 12.sp,
                )
            }
        }
        if (loan.status == LoanStatus.BORROWED) {
            OutlinedButton(
                onClick = { onReturn(loan.id) },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, StacksColors.Line),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = StacksColors.Surface,
                    contentColor = StacksColors.Ink,
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text("Return", fontFamily = StacksType.Body, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
        }
        if (isStaff && loan.status == LoanStatus.RETURNED) {
            Button(
                onClick = { onApprove(loan.id) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StacksColors.Pine,
                    contentColor = StacksColors.OnAccent,
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text("Approve", fontFamily = StacksType.Body, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
        }
    }
}

/** Small CSS-style cover for a loan card (.cover): coloured block + spine + title. */
@Composable
private fun LoanCover(bookId: String, title: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(6.dp))
            .background(loanCoverColor(bookId)),
    ) {
        Box(Modifier.fillMaxHeight().width(8.dp).background(Color.Black.copy(alpha = 0.12f)))
        Text(
            text = title,
            color = Color.White,
            fontFamily = StacksType.Display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
        )
    }
}

/** Status badge mirroring the design (.badge.ok / .badge.off). */
@Composable
fun LoanStatusChip(status: LoanStatus) {
    val (foreground, background, label) = when (status) {
        LoanStatus.BORROWED -> Triple(StacksColors.Pine, StacksColors.Sage100, "Borrowed")
        LoanStatus.RETURNED -> Triple(StacksColors.Faint, StacksColors.OutChipBg, "Returned")
        LoanStatus.APPROVED -> Triple(StacksColors.Brass700, StacksColors.Brass100, "Approved")
    }
    StatusPill(foreground, background, label)
}

@Composable
private fun StatusPill(foreground: Color, background: Color, label: String) {
    Text(
        text = label,
        color = foreground,
        fontFamily = StacksType.Body,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 9.dp, vertical = 3.dp),
    )
}

/** Deterministic spine colour per book from the design's cover palette. */
private fun loanCoverColor(bookId: String): Color {
    val palette = StacksColors.CoverPalette
    val index = ((bookId.hashCode() % palette.size) + palette.size) % palette.size
    return palette[index]
}
