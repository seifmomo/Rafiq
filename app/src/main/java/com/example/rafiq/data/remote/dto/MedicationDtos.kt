package com.example.rafiq.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MedicationDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("dosage") val dosage: String?,
    @SerializedName("time") val time: String,
    @SerializedName("created_at") val createdAt: String?
)

data class MedicationsResponse(
    @SerializedName("medications") val medications: List<MedicationDto>
)

data class MedicationResponse(
    @SerializedName("medication") val medication: MedicationDto
)

data class CreateMedicationRequest(
    @SerializedName("name") val name: String,
    @SerializedName("dosage") val dosage: String = "",
    @SerializedName("time") val time: String
)
