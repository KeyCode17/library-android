package com.library.android.presentation.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.library.android.domain.model.Book
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType
import com.library.android.presentation.ui.AvailabilityChip
import com.library.android.presentation.ui.BookCover
import com.library.android.presentation.ui.ShelfTab
import com.library.android.presentation.ui.StacksIcons

/** Book-detail body: scrollable content (.d-scroll) above the pinned actions (.d-actions). */
@Composable
fun BookDetail(book: Book, borrow: BorrowUiState = BorrowUiState(), onBorrow: () -> Unit = {}) {
    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            DetailHero(book)
            ShelfLocationCard(book)
            DetailsSection(book)
            Spacer(Modifier.height(18.dp))
        }
        DetailActions(available = book.available, borrow = borrow, onBorrow = onBorrow)
    }
}

/** .d-hero — large cover + title, author, availability. */
@Composable
private fun DetailHero(book: Book) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, top = 8.dp, end = 18.dp, bottom = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BookCover(
            book = book,
            modifier = Modifier.size(width = 108.dp, height = 144.dp),
            titleSize = 14.sp,
            showAuthor = true,
        )
        Column {
            Text(
                text = book.title,
                color = StacksColors.Ink,
                fontFamily = StacksType.Display,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 26.sp,
            )
            Text(
                text = book.author,
                color = StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
            Box(Modifier.padding(top = 12.dp)) {
                AvailabilityChip(book.available)
            }
        }
    }
}

/** .d-shelf — the hero "find it on the shelf" location block. */
@Composable
private fun ShelfLocationCard(book: Book) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(bottom = 16.dp)
            .clip(shape)
            .background(StacksColors.Surface)
            .border(1.dp, StacksColors.Line, shape)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = StacksIcons.Pin,
            contentDescription = null,
            tint = StacksColors.Brass,
            modifier = Modifier.size(18.dp),
        )
        Column(Modifier.weight(1f)) {
            Text(
                text = "FIND IT ON THE SHELF",
                color = StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
            )
            Text(
                text = "${book.shelf} · Row ${book.row}",
                color = StacksColors.Ink,
                fontFamily = StacksType.Mono,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        ShelfTab(book.shelf, book.row)
    }
}

/** .sectit "Details" + the mono detail line. Only contract-backed fields are shown. */
@Composable
private fun DetailsSection(book: Book) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
        Text(
            text = "DETAILS",
            color = StacksColors.Muted,
            fontFamily = StacksType.Body,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text(
            text = "ISBN ${book.isbn}",
            color = StacksColors.Muted,
            fontFamily = StacksType.Mono,
            fontSize = 12.sp,
        )
        // The design mock also shows a description, year, page count and copy count. None of
        // those are in the contract's Book schema, so they are omitted rather than invented
        // (anti-drift). Render them once the contract grows the fields.
    }
}

/**
 * .d-actions — Borrow (primary, wired to `POST /loans`) + Reserve. Borrow is disabled when the
 * book is unavailable or a request is in flight; result/errors surface in [BorrowUiState.message].
 * Reserve stays visual — the contract has no reservation endpoint (anti-drift).
 */
@Composable
private fun DetailActions(available: Boolean, borrow: BorrowUiState, onBorrow: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        borrow.message?.let { message ->
            Text(
                text = message,
                color = StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontSize = 13.sp,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onBorrow,
                enabled = available && !borrow.inProgress,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StacksColors.Pine,
                    contentColor = StacksColors.OnAccent,
                ),
            ) {
                val label = when {
                    borrow.inProgress -> "Borrowing…"
                    !available -> "On loan"
                    else -> "Borrow"
                }
                Text(label, fontFamily = StacksType.Body, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
            OutlinedButton(
                onClick = {},
                enabled = false,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, StacksColors.Line),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StacksColors.Ink),
            ) {
                Text("Reserve", fontFamily = StacksType.Body, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
        }
    }
}
