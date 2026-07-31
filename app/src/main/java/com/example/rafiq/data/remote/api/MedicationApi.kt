package com.example.rafiq.data.remote.api

import com.example.rafiq.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface MedicationApi {
    @GET("medications")
    suspend fun getMedications(): Response<MedicationsResponse>

    @POST("medications")
    suspend fun createMedication(@Body request: CreateMedicationRequest): Response<MedicationResponse>

    @PUT("medications/{id}")
    suspend fun updateMedication(
        @Path("id") id: String,
        @Body request: CreateMedicationRequest
    ): Response<MedicationResponse>

    @DELETE("medications/{id}")
    suspend fun deleteMedication(@Path("id") id: String): Response<StatusResponse>
}
