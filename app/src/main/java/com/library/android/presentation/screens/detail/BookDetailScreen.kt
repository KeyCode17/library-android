package com.library.android.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.library.android.domain.model.Book
import com.library.android.presentation.theme.LibraryTheme
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType
import com.library.android.presentation.ui.StacksIcons

/** Stateful book-detail screen — wires the ViewModel, delegates to stateless [BookDetailContent]. */
@Composable
fun BookDetailScreen(
    onBack: () -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BookDetailContent(
        state = state,
        onBack = onBack,
        onRetry = viewModel::load,
        modifier = Modifier.systemBarsPadding(),
    )
}

/** Pure, previewable detail UI: back app bar + the current [BookDetailUiState]. */
@Composable
fun BookDetailContent(
    state: BookDetailUiState,
    onBack: () -> Unit = {},
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        DetailAppBar(onBack)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (state) {
                BookDetailUiState.Loading -> DetailLoading()
                is BookDetailUiState.Content -> BookDetail(state.book)
                BookDetailUiState.NotFound ->
                    DetailMessage("Book not found", "We couldn't find that book.")
                is BookDetailUiState.Error -> DetailError(state.message, onRetry)
            }
        }
    }
}

/** .appbar (detail variant): "‹ Catalog" back affordance + avatar. */
@Composable
private fun DetailAppBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 6.dp, end = 18.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.clickable(onClick = onBack).padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Icon(
                imageVector = StacksIcons.NavBack,
                contentDescription = "Back to catalog",
                tint = StacksColors.Pine,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "Catalog",
                color = StacksColors.Pine,
                fontFamily = StacksType.Body,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
        }
        Box(Modifier.weight(1f))
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(StacksColors.Pine),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "DK",
                color = StacksColors.OnAccent,
                fontFamily = StacksType.Body,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun DetailLoading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = StacksColors.Pine,
            modifier = Modifier.testTag(BookDetailTestTags.LOADING),
        )
    }
}

@Composable
private fun DetailMessage(title: String, body: String) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                color = StacksColors.Ink,
                fontFamily = StacksType.Display,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = body,
                color = StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DetailError(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = message,
                color = StacksColors.Ink,
                fontFamily = StacksType.Body,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StacksColors.Pine,
                    contentColor = StacksColors.OnAccent,
                ),
            ) {
                Text("Retry", fontFamily = StacksType.Body, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
        }
    }
}

private val sampleBook =
    Book("1", "The Left Hand of Darkness", "Ursula K. Le Guin", "9780441478125", "R12", 3, true)

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun BookDetailContentPreview() {
    LibraryTheme { BookDetailContent(BookDetailUiState.Content(sampleBook)) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun BookDetailNotFoundPreview() {
    LibraryTheme { BookDetailContent(BookDetailUiState.NotFound) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun BookDetailErrorPreview() {
    LibraryTheme { BookDetailContent(BookDetailUiState.Error("Couldn't load this book")) }
}
