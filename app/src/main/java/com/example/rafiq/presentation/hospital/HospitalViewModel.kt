package com.example.rafiq.presentation.hospital

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class Hospital(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val distanceKm: Double,
    val isWheelchairAccessible: Boolean,
    val hasSignLanguageSupport: Boolean
)

@HiltViewModel
class HospitalViewModel @Inject constructor() : ViewModel() {

    private val _hospitals = MutableStateFlow<List<Hospital>>(emptyList())
    val hospitals: StateFlow<List<Hospital>> = _hospitals.asStateFlow()

    init {
        // Load mock hospitals
        _hospitals.value = listOf(
            Hospital(
                id = "1",
                name = "مستشفى القصر العيني (Kasr Al Ainy Hospital)",
                address = "Al Manial, Cairo",
                phone = "02 23644281",
                distanceKm = 2.5,
                isWheelchairAccessible = true,
                hasSignLanguageSupport = false
            ),
            Hospital(
                id = "2",
                name = "مستشفى كليوباترا (Cleopatra Hospital)",
                address = "Heliopolis, Cairo",
                phone = "19005",
                distanceKm = 4.1,
                isWheelchairAccessible = true,
                hasSignLanguageSupport = true
            ),
            Hospital(
                id = "3",
                name = "مستشفى دار الفؤاد (Dar Al Fouad Hospital)",
                address = "6th of October City, Giza",
                phone = "16370",
                distanceKm = 8.7,
                isWheelchairAccessible = true,
                hasSignLanguageSupport = true
            ),
            Hospital(
                id = "4",
                name = "المستشفى السعودي الألماني (Saudi German Hospital)",
                address = "Nozha, Cairo",
                phone = "16259",
                distanceKm = 5.2,
                isWheelchairAccessible = true,
                hasSignLanguageSupport = false
            )
        ).sortedBy { it.distanceKm }
    }
}
