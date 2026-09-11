package com.example.rafiq.presentation.bookinghistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rafiq.domain.model.BookingRecord
import com.example.rafiq.domain.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingHistoryViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    val bookings: StateFlow<List<BookingRecord>> = bookingRepository.observeBookings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun rateBooking(bookingId: String, rating: Int) {
        viewModelScope.launch {
            bookingRepository.rateBooking(bookingId, rating)
        }
    }

    fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.deleteBooking(bookingId)
        }
    }

    fun rebook(
        booking: BookingRecord,
        onRebook: (BookingRecord) -> Unit
    ) {
        onRebook(booking)
    }
}