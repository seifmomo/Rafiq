package com.example.rafiq.presentation.gamification

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.data.local.EquippedPlaceEntity
import com.example.rafiq.data.local.UserPreferences
import com.example.rafiq.domain.repository.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddPlaceViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val placeRepository: PlaceRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val totalPoints: StateFlow<Int> = userPreferences.totalPoints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    fun savePlace(
        name: String,
        description: String,
        isWheelchairAccessible: Boolean,
        hasSignLanguage: Boolean,
        hasBrailleSignage: Boolean
    ) {
        if (_saveState.value == SaveState.Saving) return

        _saveState.value = SaveState.Saving
        viewModelScope.launch {
            try {
                val location = getCurrentLocation()
                val newPlace = EquippedPlaceEntity(
                    id = UUID.randomUUID().toString(),
                    name = name.trim(),
                    description = description.trim(),
                    latitude = location.first,
                    longitude = location.second,
                    isWheelchairAccessible = isWheelchairAccessible,
                    hasSignLanguageSupport = hasSignLanguage,
                    hasBrailleSignage = hasBrailleSignage
                )
                placeRepository.insertPlace(newPlace)
                userPreferences.addPoints(50)
                _saveState.value = SaveState.Success(
                    locationCaptured = location.first != 0.0 || location.second != 0.0
                )
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(
                    e.localizedMessage ?: "Failed to save place"
                )
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(): Pair<Double, Double> {
        return try {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val providers = locationManager.getProviders(true)
            for (provider in providers) {
                try {
                    val location = locationManager.getLastKnownLocation(provider)
                    if (location != null) {
                        return Pair(location.latitude, location.longitude)
                    }
                } catch (_: SecurityException) {
                    // Permission not granted for this provider, try next
                    continue
                }
            }
            Pair(0.0, 0.0)
        } catch (_: SecurityException) {
            Pair(0.0, 0.0)
        } catch (_: Exception) {
            Pair(0.0, 0.0)
        }
    }

    sealed class SaveState {
        data object Idle : SaveState()
        data object Saving : SaveState()
        data class Success(val locationCaptured: Boolean) : SaveState()
        data class Error(val message: String) : SaveState()
    }
}
