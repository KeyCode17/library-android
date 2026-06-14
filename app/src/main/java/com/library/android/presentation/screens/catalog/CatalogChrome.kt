package com.library.android.presentation.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType
import com.library.android.presentation.ui.StacksIcons

/** App bar (.appbar): "Stacks." wordmark + circular avatar. */
@Composable
fun StacksAppBar() {
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
 * Search bar (.m-search). Visual chrome only for T-001: the contract exposes no search
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

private data class NavDestination(val label: String, val icon: ImageVector, val selected: Boolean)

/**
 * Bottom navigation (.bottomnav). Catalog is the only live destination at T-001; Borrowed
 * (M1), Chat (M3) and Profile arrive in later milestones, so the others are visual only.
 */
@Composable
fun StacksBottomNav() {
    val destinations = listOf(
        NavDestination("Catalog", StacksIcons.NavCatalog, selected = true),
        NavDestination("Borrowed", StacksIcons.NavBorrowed, selected = false),
        NavDestination("Chat", StacksIcons.NavChat, selected = false),
        NavDestination("Profile", StacksIcons.NavProfile, selected = false),
    )
    Column {
        HorizontalDivider(thickness = 1.dp, color = StacksColors.Line)
        Row(Modifier.fillMaxWidth().background(StacksColors.Surface)) {
            destinations.forEach { destination ->
                val tint = if (destination.selected) StacksColors.Pine else StacksColors.Faint
                Column(
                    modifier = Modifier.weight(1f).padding(top = 10.dp, bottom = 14.dp),
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
