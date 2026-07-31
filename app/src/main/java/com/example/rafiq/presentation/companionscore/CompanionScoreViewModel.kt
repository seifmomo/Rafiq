package com.example.rafiq.presentation.companionscore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.data.local.UserPreferences
import com.example.rafiq.data.remote.api.ScoreboardApi
import com.example.rafiq.data.remote.dto.LeaderboardEntryDto
import com.example.rafiq.data.remote.dto.MyRankResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanionScoreViewModel @Inject constructor(
    userPreferences: UserPreferences,
    private val scoreboardApi: ScoreboardApi
) : ViewModel() {

    val totalPoints: StateFlow<Int> = userPreferences.totalPoints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntryDto>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntryDto>> = _leaderboard.asStateFlow()

    private val _myRank = MutableStateFlow<MyRankResponse?>(null)
    val myRank: StateFlow<MyRankResponse?> = _myRank.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadLeaderboard()
        loadMyRank()
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = scoreboardApi.getLeaderboard(100, 0)
                if (response.isSuccessful) {
                    _leaderboard.value = response.body()?.leaderboard ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = "Could not load leaderboard"
            }
            _isLoading.value = false
        }
    }

    fun loadMyRank() {
        viewModelScope.launch {
            try {
                val response = scoreboardApi.getMyRank()
                if (response.isSuccessful) {
                    _myRank.value = response.body()
                }
            } catch (_: Exception) {}
        }
    }
}
