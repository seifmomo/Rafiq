package com.example.rafiq.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.data.local.EquippedPlaceEntity
import com.example.rafiq.domain.repository.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val placeRepository: PlaceRepository
) : ViewModel() {

    val places: StateFlow<List<EquippedPlaceEntity>> = placeRepository.getAllPlaces()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Seed a few demo places so the map is never empty on a fresh install
        viewModelScope.launch {
            if (placeRepository.getAllPlaces().first().isEmpty()) {
                DEMO_PLACES.forEach { placeRepository.insertPlace(it) }
            }
        }
    }

    private companion object {
        val DEMO_PLACES = listOf(
            EquippedPlaceEntity(
                id = "demo_cairo_hospital",
                name = "Cairo Accessibility Center",
                description = "Wheelchair ramps, sign-language interpreters and Braille guidance available.",
                latitude = 30.0444,
                longitude = 31.2357,
                isWheelchairAccessible = true,
                hasSignLanguageSupport = true,
                hasBrailleSignage = true
            ),
            EquippedPlaceEntity(
                id = "demo_giza_library",
                name = "Giza Inclusive Library",
                description = "Accessible entrances, tactile paving and inclusive reading spaces.",
                latitude = 29.9870,
                longitude = 31.2118,
                isWheelchairAccessible = true,
                hasSignLanguageSupport = false,
                hasBrailleSignage = true
            ),
            EquippedPlaceEntity(
                id = "demo_nasr_hospital",
                name = "Nasr City Hearing Center",
                description = "Sign-language staff and accessible consultation rooms.",
                latitude = 30.0595,
                longitude = 31.3302,
                isWheelchairAccessible = true,
                hasSignLanguageSupport = true,
                hasBrailleSignage = false
            )
        )
    }
}