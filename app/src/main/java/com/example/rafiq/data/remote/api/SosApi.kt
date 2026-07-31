package com.example.rafiq.data.remote.api

import com.example.rafiq.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface SosApi {
    @POST("sos/alert")
    suspend fun createAlert(@Body request: CreateSosAlertRequest): Response<SosAlertResponse>

    @GET("sos/alerts")
    suspend fun getAlerts(): Response<SosAlertsResponse>

    @GET("sos/active")
    suspend fun getActiveAlert(): Response<ActiveAlertResponse>

    @PUT("sos/alerts/{id}/resolve")
    suspend fun resolveAlert(@Path("id") id: String): Response<SosAlertResponse>
}
