package edu.rit.dk9612.resonancetv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.rit.dk9612.resonancetv.data.model.VideoCategory
import edu.rit.dk9612.resonancetv.data.repository.YouTubeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Define the possible states of our Home Screen
sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val categories: List<VideoCategory>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel : ViewModel() {

    // 2. Grab our clean repository
    private val repository = YouTubeRepository()

    // 3. Set up the StateFlow that the UI will observe
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchHomeContent()
    }

    fun fetchHomeContent() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading // Tell UI to show a spinner

            try {
                // Ask the repository for the data
                val categories = repository.getHomeCategories()

                if (categories.isNotEmpty()) {
                    _uiState.value = HomeUiState.Success(categories)
                } else {
                    _uiState.value = HomeUiState.Error("No videos found. Check your network or API quota.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = HomeUiState.Error("Network Error: ${e.localizedMessage}")
            }
        }
    }
}