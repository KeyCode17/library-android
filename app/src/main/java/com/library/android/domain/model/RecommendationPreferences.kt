package com.library.android.domain.model

/** User preferences the recommender ranks against (mirrors the FFI PreferencesDto, sans binding types). */
data class RecommendationPreferences(
    val preferredShelves: List<String> = emptyList(),
    val preferredAuthors: List<String> = emptyList(),
    val availableOnly: Boolean = false,
)
