package com.example.rafiq.data.remote.api

import com.example.rafiq.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ScoreboardApi {
    @GET("scoreboard")
    suspend fun getLeaderboard(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<LeaderboardResponse>

    @GET("scoreboard/my-rank")
    suspend fun getMyRank(): Response<MyRankResponse>

    @GET("scoreboard/history")
    suspend fun getScoreHistory(): Response<ScoreHistoryResponse>

    @POST("scoreboard/add-points")
    suspend fun addPoints(@Body request: AddPointsRequest): Response<AddPointsResponse>
}
