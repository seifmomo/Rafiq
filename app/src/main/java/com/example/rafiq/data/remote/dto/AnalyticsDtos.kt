package com.example.rafiq.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AnalyticsEventRequest(
    @SerializedName("activityType") val activityType: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("metadata") val metadata: Map<String, Any> = emptyMap()
)

data class StatsResponse(
    @SerializedName("stats") val stats: StatsDto
)

data class StatsDto(
    @SerializedName("total_chat_messages") val totalChatMessages: Int?,
    @SerializedName("chat_points_earned") val chatPointsEarned: Int?,
    @SerializedName("total_sos_alerts") val totalSosAlerts: Int?,
    @SerializedName("total_places") val totalPlaces: Int?,
    @SerializedName("total_contacts") val totalContacts: Int?,
    @SerializedName("total_medications") val totalMedications: Int?,
    @SerializedName("total_score_events") val totalScoreEvents: Int?,
    @SerializedName("current_points") val currentPoints: Int?
)
