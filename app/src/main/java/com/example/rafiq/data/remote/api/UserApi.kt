package com.example.rafiq.data.remote.api

import com.example.rafiq.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @GET("users/profile")
    suspend fun getProfile(): Response<UserResponse>

    @PUT("users/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserResponse>

    @PUT("users/disability-type")
    suspend fun updateDisabilityType(@Body request: DisabilityTypeRequest): Response<UserResponse>

    @PUT("users/fcm-token")
    suspend fun updateFcmToken(@Body request: FcmTokenRequest): Response<StatusResponse>

    @PUT("users/guardian-mode")
    suspend fun updateGuardianMode(@Body request: GuardianModeRequest): Response<GuardianModeResponse>
}

data class GuardianModeResponse(
    val guardianMode: Boolean
)
