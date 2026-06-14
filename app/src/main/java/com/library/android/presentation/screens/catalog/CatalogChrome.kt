package com.library.android.presentation.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType
import com.library.android.presentation.ui.StacksIcons

/** App bar (.appbar): "Stacks." wordmark + a "For you" recommendations entry + avatar. */
@Composable
fun StacksAppBar(onRecommendationsClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, top = 6.dp, end = 18.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = buildAnnotatedString {
                append("Stacks")
                withStyle(SpanStyle(color = StacksColors.Pine)) { append(".") }
            },
            color = StacksColors.Ink,
            fontFamily = StacksType.Display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = "For you",
            color = StacksColors.Pine,
            fontFamily = StacksType.Body,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            modifier = Modifier
                .clickable(onClick = onRecommendationsClick)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
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

/**
 * Book-finder (shelf/row) — the contract-backed `GET /books?shelf&row` filter. Clean default
 * layout: `catalog.html` shows no shelf/row finder, so this control needs a design pass.
 * Typed text is ephemeral UI state (`remember`); the applied filter lives in the ViewModel.
 */
@Composable
fun CatalogFinderBar(
    filter: CatalogFilter,
    onApply: (String?, Int?) -> Unit,
    onClear: () -> Unit,
) {
    var shelfText by remember(filter) { mutableStateOf(filter.shelf.orEmpty()) }
    var rowText by remember(filter) { mutableStateOf(filter.row?.toString().orEmpty()) }
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp).padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = shelfText,
                onValueChange = { shelfText = it },
                label = { Text("Shelf") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = rowText,
                onValueChange = { new -> rowText = new.filter { it.isDigit() } },
                label = { Text("Row") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(110.dp),
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = { onApply(shelfText, rowText.toIntOrNull()) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StacksColors.Pine,
                    contentColor = StacksColors.OnAccent,
                ),
            ) {
                Text("Find", fontFamily = StacksType.Body, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
            if (filter.isActive) {
                TextButton(onClick = onClear) {
                    Text(
                        text = "Clear",
                        color = StacksColors.Pine,
                        fontFamily = StacksType.Body,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

/**
 * Search bar (.m-search). Visual chrome only: the contract exposes no free-text search
 * parameter, so wiring it would invent API surface (anti-drift). Becomes a real field when
 * the contract gains search.
 */
@Composable
fun CatalogSearchBar() {
    val shape = RoundedCornerShape(999.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(bottom = 12.dp)
            .clip(shape)
            .background(StacksColors.Surface)
            .border(1.dp, StacksColors.Line, shape)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(StacksIcons.Search, contentDescription = null, tint = StacksColors.Muted, modifier = Modifier.size(16.dp))
        Text("Search the catalog", color = StacksColors.Muted, fontFamily = StacksType.Body, fontSize = 15.sp)
    }
}

private data class Filter(val label: String, val selected: Boolean)

/**
 * Filter chips (.m-chips). Visual only for T-001 — the contract has no category/availability
 * filter parameter yet, so these are not wired to the backend.
 */
@Composable
fun FilterChipsRow() {
    val filters = listOf(
        Filter("All", selected = true),
        Filter("Available", selected = false),
        Filter("Fiction", selected = false),
        Filter("Science", selected = false),
        Filter("Reference", selected = false),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(bottom = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        filters.forEach { CatalogFilterChip(it) }
    }
}

@Composable
private fun CatalogFilterChip(filter: Filter) {
    val shape = RoundedCornerShape(999.dp)
    val background = if (filter.selected) StacksColors.Pine else StacksColors.Surface
    val foreground = if (filter.selected) StacksColors.OnAccent else StacksColors.Muted
    val borderColor = if (filter.selected) StacksColors.Pine else StacksColors.Line
    Box(
        modifier = Modifier
            .clip(shape)
            .background(background)
            .border(1.dp, borderColor, shape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = filter.label,
            color = foreground,
            fontFamily = StacksType.Body,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
        )
    }
}

private data class NavDestination(
    val label: String,
    val icon: ImageVector,
    val selected: Boolean,
    val onClick: () -> Unit,
)

/**
 * Bottom navigation (.bottomnav). Catalog (current) and Profile (auth) are live; Borrowed
 * (M1) and Chat (M3) arrive in later milestones, so they stay visual only.
 */
@Composable
fun StacksBottomNav(onProfileClick: () -> Unit, onBorrowedClick: () -> Unit) {
    val destinations = listOf(
        NavDestination("Catalog", StacksIcons.NavCatalog, selected = true, onClick = {}),
        NavDestination("Borrowed", StacksIcons.NavBorrowed, selected = false, onClick = onBorrowedClick),
        NavDestination("Chat", StacksIcons.NavChat, selected = false, onClick = {}),
        NavDestination("Profile", StacksIcons.NavProfile, selected = false, onClick = onProfileClick),
    )
    Column {
        HorizontalDivider(thickness = 1.dp, color = StacksColors.Line)
        Row(Modifier.fillMaxWidth().background(StacksColors.Surface)) {
            destinations.forEach { destination ->
                val tint = if (destination.selected) StacksColors.Pine else StacksColors.Faint
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = destination.onClick)
                        .padding(top = 10.dp, bottom = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                        tint = tint,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = destination.label,
                        color = tint,
                        fontFamily = StacksType.Body,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                    )
                }
            }
        }
    }
}
