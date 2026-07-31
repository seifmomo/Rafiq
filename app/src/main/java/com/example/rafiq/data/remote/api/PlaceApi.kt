package com.example.rafiq.data.remote.api

import com.example.rafiq.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface PlaceApi {
    @GET("places")
    suspend fun getPlaces(@Query("public") public: Boolean = true): Response<PlacesResponse>

    @POST("places")
    suspend fun createPlace(@Body request: CreatePlaceRequest): Response<PlaceResponse>

    @PUT("places/{id}")
    suspend fun updatePlace(
        @Path("id") id: String,
        @Body request: CreatePlaceRequest
    ): Response<PlaceResponse>

    @DELETE("places/{id}")
    suspend fun deletePlace(@Path("id") id: String): Response<StatusResponse>
}
