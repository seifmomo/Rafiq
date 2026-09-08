package com.example.rafiq.data.remote.api

import com.example.rafiq.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @PUT("users/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserResponse>

    @PUT("users/guardian-mode")
    suspend fun updateGuardianMode(@Body request: GuardianModeRequest): Response<GuardianModeResponse>
}

data class GuardianModeResponse(
    val guardianMode: Boolean
)
