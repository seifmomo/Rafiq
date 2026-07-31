package com.example.rafiq.data.remote.api

import com.example.rafiq.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ChatApi {
    @GET("chat/messages")
    suspend fun getMessages(
        @Query("limit") limit: Int = 100,
        @Query("before") before: String? = null
    ): Response<MessagesResponse>

    @POST("chat/messages")
    suspend fun sendMessage(@Body request: CreateMessageRequest): Response<MessageResponse>

    @POST("chat/sync")
    suspend fun syncMessages(@Body request: SyncMessagesRequest): Response<SyncMessagesResponse>

    @DELETE("chat/messages/{id}")
    suspend fun deleteMessage(@Path("id") id: String): Response<StatusResponse>
}
