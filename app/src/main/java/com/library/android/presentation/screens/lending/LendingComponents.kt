package com.library.android.presentation.screens.lending

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

/** A loan row: book reference + status + due date, with role-aware return/approve actions. */
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
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "Book ${loan.bookId}",
                color = StacksColors.Ink,
                fontFamily = StacksType.Mono,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LoanStatusChip(loan.status)
                Text(
                    text = "Due ${loan.dueAt.substringBefore('T')}",
                    color = StacksColors.Muted,
                    fontFamily = StacksType.Body,
                    fontSize = 12.sp,
                )
            }
        }
        if (loan.status == LoanStatus.BORROWED) {
            TextButton(onClick = { onReturn(loan.id) }) {
                Text("Return", color = StacksColors.Pine, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
        }
        if (isStaff && loan.status == LoanStatus.RETURNED) {
            TextButton(onClick = { onApprove(loan.id) }) {
                Text("Approve", color = StacksColors.Pine, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
        }
    }
}

/** Status pill mirroring the catalog availability style. */
@Composable
fun LoanStatusChip(status: LoanStatus) {
    val (foreground, background, label) = when (status) {
        LoanStatus.BORROWED -> Triple(StacksColors.Pine, StacksColors.Sage100, "Borrowed")
        LoanStatus.RETURNED -> Triple(StacksColors.Brass700, StacksColors.Brass100, "Returned")
        LoanStatus.APPROVED -> Triple(StacksColors.Faint, StacksColors.OutChipBg, "Approved")
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
        fontSize = 12.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 9.dp, vertical = 3.dp),
    )
}
