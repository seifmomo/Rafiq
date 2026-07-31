package com.example.rafiq.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PlaceDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("is_wheelchair_accessible") val isWheelchairAccessible: Boolean?,
    @SerializedName("has_sign_language_support") val hasSignLanguageSupport: Boolean?,
    @SerializedName("has_braille_signage") val hasBrailleSignage: Boolean?,
    @SerializedName("is_public") val isPublic: Boolean?,
    @SerializedName("created_at") val createdAt: String?
)

data class PlacesResponse(
    @SerializedName("places") val places: List<PlaceDto>
)

data class PlaceResponse(
    @SerializedName("place") val place: PlaceDto
)

data class CreatePlaceRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("isWheelchairAccessible") val isWheelchairAccessible: Boolean = false,
    @SerializedName("hasSignLanguageSupport") val hasSignLanguageSupport: Boolean = false,
    @SerializedName("hasBrailleSignage") val hasBrailleSignage: Boolean = false,
    @SerializedName("isPublic") val isPublic: Boolean = true
)
