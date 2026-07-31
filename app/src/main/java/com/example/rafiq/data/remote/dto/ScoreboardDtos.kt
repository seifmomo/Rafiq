package com.example.rafiq.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LeaderboardResponse(
    @SerializedName("leaderboard") val leaderboard: List<LeaderboardEntryDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("offset") val offset: Int
)

data class LeaderboardEntryDto(
    @SerializedName("id") val id: String,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("disability_type") val disabilityType: String?,
    @SerializedName("total_points") val totalPoints: Int
)

data class RankResponse(
    @SerializedName("userId") val userId: String,
    @SerializedName("totalPoints") val totalPoints: Int,
    @SerializedName("rank") val rank: Int
)

data class MyRankResponse(
    @SerializedName("rank") val rank: Int,
    @SerializedName("totalPoints") val totalPoints: Int,
    @SerializedName("nearbyAbove") val nearbyAbove: List<LeaderboardEntryDto>,
    @SerializedName("nearbyBelow") val nearbyBelow: List<LeaderboardEntryDto>
)

data class ScoreHistoryResponse(
    @SerializedName("events") val events: List<ScoreEventDto>
)

data class ScoreEventDto(
    @SerializedName("id") val id: String,
    @SerializedName("points") val points: Int,
    @SerializedName("reason") val reason: String,
    @SerializedName("created_at") val createdAt: String
)

data class AddPointsRequest(
    @SerializedName("points") val points: Int,
    @SerializedName("reason") val reason: String
)

data class AddPointsResponse(
    @SerializedName("totalPoints") val totalPoints: Int,
    @SerializedName("pointsAdded") val pointsAdded: Int
)
