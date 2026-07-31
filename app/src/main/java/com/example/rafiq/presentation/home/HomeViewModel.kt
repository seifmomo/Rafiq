package com.example.rafiq.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.data.local.UserPreferences
import com.example.rafiq.data.remote.api.UserApi
import com.example.rafiq.data.remote.dto.DisabilityTypeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    userPreferences: UserPreferences,
    private val userApi: UserApi
) : ViewModel() {

    val totalPoints: StateFlow<Int> = userPreferences.totalPoints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun syncDisabilityType(type: String) {
        viewModelScope.launch {
            try {
                userApi.updateDisabilityType(DisabilityTypeRequest(type))
            } catch (_: Exception) {
                // silently fail - disability type is saved locally anyway
            }
        }
    }
}
