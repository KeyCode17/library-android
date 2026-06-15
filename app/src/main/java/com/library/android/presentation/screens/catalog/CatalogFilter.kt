package com.library.android.presentation.screens.catalog

/** Active catalog filter sent to `GET /books`. Text search + shelf/row finder, all combinable. */
data class CatalogFilter(
    val shelf: String? = null,
    val row: Int? = null,
    val query: String? = null,
) {
    val isActive: Boolean get() = shelf != null || row != null || query != null
}
