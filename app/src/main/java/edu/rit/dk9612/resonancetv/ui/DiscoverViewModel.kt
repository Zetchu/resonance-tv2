package edu.rit.dk9612.resonancetv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.rit.dk9612.resonancetv.data.model.VideoItem
import edu.rit.dk9612.resonancetv.data.repository.YouTubeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DiscoverViewModel : ViewModel() {
    private val repository = YouTubeRepository()
    
    private val _searchResults = MutableStateFlow<List<VideoItem>>(emptyList())
    val searchResults: StateFlow<List<VideoItem>> = _searchResults

    val genres = listOf("Hard Bounce", "Speed Garage", "UKG", "Minimal", "Warehouse")
    init {
        searchByGenre("240kmh")
    }
    fun searchByGenre(genre: String) {
        viewModelScope.launch {
            _searchResults.value = repository.searchByGenre(genre)
        }
    }
}