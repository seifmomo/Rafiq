package com.example.rafiq.data.remote.api

import com.example.rafiq.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ContactApi {
    @GET("contacts")
    suspend fun getContacts(): Response<ContactsResponse>

    @POST("contacts")
    suspend fun createContact(@Body request: CreateContactRequest): Response<ContactResponse>

    @PUT("contacts/{id}")
    suspend fun updateContact(
        @Path("id") id: String,
        @Body request: CreateContactRequest
    ): Response<ContactResponse>

    @DELETE("contacts/{id}")
    suspend fun deleteContact(@Path("id") id: String): Response<StatusResponse>
}
