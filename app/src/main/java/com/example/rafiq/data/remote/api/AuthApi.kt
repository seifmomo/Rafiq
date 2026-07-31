package com.example.rafiq.data.remote.api

import com.example.rafiq.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/guest")
    suspend fun guestLogin(@Body request: GuestRequest): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getMe(): Response<UserResponse>

    @PUT("auth/password")
    suspend fun changePassword(@Body request: PasswordChangeRequest): Response<StatusResponse>

    @DELETE("auth/account")
    suspend fun deleteAccount(): Response<StatusResponse>
}
