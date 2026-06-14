package com.library.android.presentation.screens.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.library.android.domain.model.RecommendationPreferences
import com.library.android.domain.recommend.Recommender
import com.library.android.domain.usecase.GetBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Builds the candidate set from the catalog the app already has and ranks it **on-device** via
 * the [Recommender] port. Idle until the user asks; UDF (private mutable in, read-only out).
 */
@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    private val getBooks: GetBooksUseCase,
    private val recommender: Recommender,
) : ViewModel() {

    private val _state = MutableStateFlow<RecommendationsUiState>(RecommendationsUiState.Idle)
    val state: StateFlow<RecommendationsUiState> = _state.asStateFlow()

    fun recommend(preferences: RecommendationPreferences) {
        viewModelScope.launch {
            _state.value = RecommendationsUiState.Loading
            getBooks()
                .onSuccess { candidates ->
                    recommender.rank(preferences, candidates)
                        .onSuccess { ranked ->
                            _state.value = if (ranked.isEmpty()) {
                                RecommendationsUiState.Empty
                            } else {
                                RecommendationsUiState.Loaded(ranked)
                            }
                        }
                        .onFailure { _state.value = RecommendationsUiState.Error(it.message ?: DEFAULT_ERROR) }
                }
                .onFailure { _state.value = RecommendationsUiState.Error(it.message ?: DEFAULT_ERROR) }
        }
    }

    private companion object {
        const val DEFAULT_ERROR = "Couldn't build recommendations"
    }
}
