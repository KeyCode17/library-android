package com.library.android.presentation.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

/** Stateful catalog screen — wires the ViewModel and delegates to the stateless [CatalogContent]. */
@Composable
fun CatalogScreen(viewModel: CatalogViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CatalogContent(
        state = state,
        onRetry = viewModel::load,
        modifier = Modifier.systemBarsPadding(),
    )
}

/** Pure, previewable catalog UI: chrome + the current [CatalogUiState]. */
@Composable
fun CatalogContent(
    state: CatalogUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        StacksAppBar()
        CatalogSearchBar()
        FilterChipsRow()
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (state) {
                CatalogUiState.Loading -> LoadingState()
                is CatalogUiState.Content -> BookList(state.books)
                CatalogUiState.Empty -> EmptyState()
                is CatalogUiState.Error -> ErrorState(state.message, onRetry)
            }
        }
        StacksBottomNav()
    }
}

@Composable
private fun BookList(books: List<Book>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(books, key = { it.id }) { book -> BookRow(book) }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = StacksColors.Pine,
            modifier = Modifier.testTag(CatalogTestTags.LOADING),
        )
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Nothing on the shelves yet",
                color = StacksColors.Ink,
                fontFamily = StacksType.Display,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "New books are on the way — check back soon.",
                color = StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
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

/** Sample catalog used by previews and shared with UI tests. */
internal val sampleBooks = listOf(
    Book("1", "The Left Hand of Darkness", "Ursula K. Le Guin", "9780441478125", "R12", 3, true),
    Book("2", "Pale Fire", "Vladimir Nabokov", "9780679723424", "R08", 1, false),
    Book("3", "Gödel, Escher, Bach", "Douglas Hofstadter", "9780465026562", "R21", 5, true),
    Book("4", "The Order of Time", "Carlo Rovelli", "9780735216105", "R19", 2, true),
    Book("5", "Wayfinding", "M. R. O'Connor", "9781250096968", "R04", 6, false),
    Book("6", "A Pattern Language", "Christopher Alexander", "9780195019193", "R31", 1, true),
)

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun CatalogContentPreview() {
    LibraryTheme { CatalogContent(CatalogUiState.Content(sampleBooks), onRetry = {}) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun CatalogLoadingPreview() {
    LibraryTheme { CatalogContent(CatalogUiState.Loading, onRetry = {}) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun CatalogEmptyPreview() {
    LibraryTheme { CatalogContent(CatalogUiState.Empty, onRetry = {}) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun CatalogErrorPreview() {
    LibraryTheme { CatalogContent(CatalogUiState.Error("Couldn't load the catalog"), onRetry = {}) }
}
