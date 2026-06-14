package com.library.android.presentation.screens.catalog

/** Active book-finder filter sent to `GET /books`. Both fields optional and combinable. */
data class CatalogFilter(
    val shelf: String? = null,
    val row: Int? = null,
) {
    val isActive: Boolean get() = shelf != null || row != null
}
