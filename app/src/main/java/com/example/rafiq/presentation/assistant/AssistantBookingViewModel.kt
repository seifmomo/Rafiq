package com.example.rafiq.presentation.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.data.repository.AssistantRepositoryImpl
import com.example.rafiq.domain.model.Assistant
import com.example.rafiq.domain.model.Booking
import com.example.rafiq.domain.model.BookingRequest
import com.example.rafiq.domain.model.DisabilityNeed
import com.example.rafiq.domain.model.MatchExplanation
import com.example.rafiq.domain.repository.AssistantRepository
import com.example.rafiq.domain.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class AssistantBookingUiState(
    val step: Int = AssistantBookingSteps.FORM,
    val selectedNeeds: Set<DisabilityNeed> = emptySet(),
    val fromLocation: String = "",
    val toLocation: String = "",
    val preferredDate: String = "",
    val preferredTime: String = "",
    val budgetPerHour: Float = 150f,
    val isRecurring: Boolean = false,
    val assistants: List<Assistant> = emptyList(),
    val explanations: Map<String, MatchExplanation> = emptyMap(),
    val selectedAssistant: Assistant? = null,
    val booking: Booking? = null,
    val savedBookingId: String? = null,
    val searching: Boolean = false,
    val error: String? = null
)

object AssistantBookingSteps {
    const val FORM = 0
    const val RESULTS = 1
    const val SUMMARY = 2
    const val CONFIRMED = 3
}

@HiltViewModel
class AssistantBookingViewModel @Inject constructor(
    private val assistantRepository: AssistantRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantBookingUiState())
    val uiState: StateFlow<AssistantBookingUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.US)

    init {
        val now = Calendar.getInstance()
        now.add(Calendar.HOUR_OF_DAY, 1)
        _uiState.update {
            it.copy(
                preferredDate = dateFormat.format(now.time),
                preferredTime = timeFormat.format(now.time)
            )
        }
    }

    fun toggleNeed(need: DisabilityNeed) {
        _uiState.update { state ->
            val updated = if (need in state.selectedNeeds) {
                state.selectedNeeds - need
            } else {
                state.selectedNeeds + need
            }
            state.copy(selectedNeeds = updated)
        }
    }

    fun setFromLocation(value: String) = _uiState.update { it.copy(fromLocation = value) }

    fun setToLocation(value: String) = _uiState.update { it.copy(toLocation = value) }

    fun setDate(epochMillis: Long?) {
        val formatted = epochMillis?.let(dateFormat::format) ?: return
        _uiState.update { it.copy(preferredDate = formatted) }
    }

    fun setTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        _uiState.update { it.copy(preferredTime = timeFormat.format(calendar.time)) }
    }

    fun setBudgetPerHour(value: Float) = _uiState.update { it.copy(budgetPerHour = value) }

    fun toggleRecurring() = _uiState.update { it.copy(isRecurring = !it.isRecurring) }

    fun canStartSearch(): Boolean {
        val state = _uiState.value
        return state.selectedNeeds.isNotEmpty() &&
            state.fromLocation.isNotBlank() &&
            state.toLocation.isNotBlank() &&
            state.preferredDate.isNotBlank() &&
            state.preferredTime.isNotBlank()
    }

    fun findAssistants() {
        val state = _uiState.value
        if (!canStartSearch()) return

        _uiState.update { it.copy(searching = true) }
        viewModelScope.launch {
            val request = BookingRequest(
                needs = state.selectedNeeds,
                fromLocation = state.fromLocation.trim(),
                toLocation = state.toLocation.trim(),
                preferredDate = state.preferredDate,
                preferredTime = state.preferredTime,
                budgetPerHour = state.budgetPerHour.toDouble(),
                isRecurring = state.isRecurring
            )
            val results = assistantRepository.findAssistants(request)
            val explanations = results.associateWith { assistantRepository.explainMatch(it, request) }
            _uiState.update {
                it.copy(
                    assistants = results,
                    explanations = explanations.entries.associate { (a, e) -> a.id to e },
                    searching = false,
                    step = AssistantBookingSteps.RESULTS
                )
            }
        }
    }

    fun selectAssistant(assistant: Assistant) {
        _uiState.update { it.copy(selectedAssistant = assistant, step = AssistantBookingSteps.SUMMARY) }
    }

    fun confirmBooking() {
        val state = _uiState.value
        val assistant = state.selectedAssistant ?: return

        val hours = AssistantRepositoryImpl.estimateHours(assistant.distanceKm)
        val cost = (hours * assistant.hourlyPrice).toInt()

        val request = BookingRequest(
            needs = state.selectedNeeds,
            fromLocation = state.fromLocation.trim(),
            toLocation = state.toLocation.trim(),
            preferredDate = state.preferredDate,
            preferredTime = state.preferredTime,
            budgetPerHour = state.budgetPerHour.toDouble(),
            isRecurring = state.isRecurring
        )
        val refDate = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val booking = Booking(
            id = "RAFIQ-$refDate-${assistant.id.uppercase()}",
            assistant = assistant,
            request = request,
            estimatedHours = hours,
            estimatedCost = cost.toDouble()
        )
        _uiState.update { it.copy(booking = booking, step = AssistantBookingSteps.CONFIRMED) }

        viewModelScope.launch {
            runCatching { bookingRepository.saveBooking(booking) }
                .onSuccess { saved ->
                    _uiState.update { it.copy(savedBookingId = saved.id) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun rateBooking(rating: Int) {
        val id = _uiState.value.savedBookingId ?: return
        viewModelScope.launch {
            runCatching { bookingRepository.rateBooking(id, rating) }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun goBack() {
        _uiState.update { state ->
            if (state.step > AssistantBookingSteps.FORM) {
                state.copy(
                    step = state.step - 1,
                    selectedAssistant = if (state.step == AssistantBookingSteps.SUMMARY) null else state.selectedAssistant
                )
            } else state
        }
    }

    fun resetAll() {
        _uiState.value = AssistantBookingUiState()
        val now = Calendar.getInstance()
        now.add(Calendar.HOUR_OF_DAY, 1)
        _uiState.update {
            it.copy(
                preferredDate = dateFormat.format(now.time),
                preferredTime = timeFormat.format(now.time)
            )
        }
    }
}