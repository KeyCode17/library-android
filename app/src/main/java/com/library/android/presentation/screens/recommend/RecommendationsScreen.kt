@file:Suppress("TooManyFunctions") // Compose screen: many small, previewable composables

package com.library.android.presentation.screens.recommend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.library.android.domain.model.Book
import com.library.android.domain.model.RecommendationPreferences
import com.library.android.presentation.screens.auth.AuthTopBar
import com.library.android.presentation.screens.catalog.BookRow
import com.library.android.presentation.theme.LibraryTheme
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType

/** Stateful recommendations screen (on-device ranking). */
@Composable
fun RecommendationsScreen(
    onBack: () -> Unit,
    onBookClick: (String) -> Unit,
    viewModel: RecommendationsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RecommendationsContent(
        state = state,
        onRecommend = viewModel::recommend,
        onBookClick = onBookClick,
        onBack = onBack,
        modifier = Modifier.systemBarsPadding(),
    )
}

/** Pure, previewable recommendations UI: a preferences form above the ranked result states. */
@Composable
fun RecommendationsContent(
    state: RecommendationsUiState,
    onRecommend: (RecommendationPreferences) -> Unit = {},
    onBookClick: (String) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("For you", onBack)
        ScreenSubtitle("Ranked on-device from your reading.")
        PreferencesForm(onRecommend)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (state) {
                RecommendationsUiState.Idle ->
                    CenteredMessage("Set your preferences, then tap Recommend.")
                RecommendationsUiState.Loading -> LoadingRecommendations()
                RecommendationsUiState.Empty ->
                    CenteredMessage("No matches — try different preferences.")
                is RecommendationsUiState.Error -> CenteredMessage(state.message)
                is RecommendationsUiState.Loaded -> RankedList(state.books, onBookClick)
            }
        }
    }
}

/** The `.sub` lead-in line under the app bar. */
@Composable
private fun ScreenSubtitle(text: String) {
    Text(
        text = text,
        color = StacksColors.Muted,
        fontFamily = StacksType.Body,
        fontSize = 14.sp,
        modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, bottom = 8.dp),
    )
}

/** The `.sectit` uppercase section label above the ranked list. */
@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = StacksColors.Muted,
        fontFamily = StacksType.Body,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 0.07.em,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, top = 2.dp),
    )
}

@Composable
private fun PreferencesForm(onRecommend: (RecommendationPreferences) -> Unit) {
    var shelves by remember { mutableStateOf("") }
    var authors by remember { mutableStateOf("") }
    var availableOnly by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = shelves,
            onValueChange = { shelves = it },
            label = { Text("Preferred shelves (comma-separated)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = authors,
            onValueChange = { authors = it },
            label = { Text("Preferred authors (comma-separated)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Available only",
                color = StacksColors.Ink,
                fontFamily = StacksType.Body,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = availableOnly,
                onCheckedChange = { availableOnly = it },
                colors = SwitchDefaults.colors(checkedTrackColor = StacksColors.Pine),
            )
        }
        Button(
            onClick = {
                onRecommend(
                    RecommendationPreferences(
                        preferredShelves = shelves.toTags(),
                        preferredAuthors = authors.toTags(),
                        availableOnly = availableOnly,
                    ),
                )
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StacksColors.Pine,
                contentColor = StacksColors.OnAccent,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Recommend", fontFamily = StacksType.Body, fontWeight = FontWeight.Medium, fontSize = 15.sp)
        }
    }
}

@Composable
private fun RankedList(books: List<Book>, onBookClick: (String) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        SectionTitle("RECOMMENDED FOR YOU")
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(books, key = { it.id }) { book ->
                BookRow(book = book, onClick = { onBookClick(book.id) })
            }
        }
    }
}

@Composable
private fun LoadingRecommendations() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = StacksColors.Pine,
            modifier = Modifier.testTag(RecommendTestTags.LOADING),
        )
    }
}

@Composable
private fun CenteredMessage(text: String) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            color = StacksColors.Muted,
            fontFamily = StacksType.Body,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }
}

private fun String.toTags(): List<String> =
    split(",").map { it.trim() }.filter { it.isNotEmpty() }

private val sampleRanked = listOf(
    Book("1", "The Left Hand of Darkness", "Ursula K. Le Guin", "9780441478125", "R12", 3, true),
    Book("2", "The Order of Time", "Carlo Rovelli", "9780735216105", "R19", 2, true),
)

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun RecommendationsLoadedPreview() {
    LibraryTheme { RecommendationsContent(RecommendationsUiState.Loaded(sampleRanked)) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun RecommendationsIdlePreview() {
    LibraryTheme { RecommendationsContent(RecommendationsUiState.Idle) }
}
