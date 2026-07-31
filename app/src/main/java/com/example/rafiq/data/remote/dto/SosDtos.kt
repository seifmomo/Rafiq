package com.example.rafiq.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SosAlertDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("location_url") val locationUrl: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("target_contact") val targetContact: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("resolved_at") val resolvedAt: String?
)

data class SosAlertResponse(
    @SerializedName("alert") val alert: SosAlertDto
)

data class SosAlertsResponse(
    @SerializedName("alerts") val alerts: List<SosAlertDto>
)

data class ActiveAlertResponse(
    @SerializedName("activeAlert") val activeAlert: SosAlertDto?
)

data class CreateSosAlertRequest(
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("targetContact") val targetContact: String?
)
