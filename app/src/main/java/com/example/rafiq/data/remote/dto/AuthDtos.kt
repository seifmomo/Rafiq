package com.example.rafiq.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("displayName") val displayName: String = ""
)

data class GuestRequest(
    @SerializedName("guest") val guest: Boolean = true
)

data class AuthResponse(
    @SerializedName("user") val user: UserDto,
    @SerializedName("token") val token: String
)

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("disability_type") val disabilityType: String?,
    @SerializedName("disability_other") val disabilityOther: String?,
    @SerializedName("emergency_contact") val emergencyContact: String?,
    @SerializedName("guardian_mode") val guardianMode: Boolean?,
    @SerializedName("language") val language: String?,
    @SerializedName("dark_theme") val darkTheme: String?,
    @SerializedName("font_size") val fontSize: String?,
    @SerializedName("font_family") val fontFamily: String?,
    @SerializedName("speech_rate") val speechRate: Double?,
    @SerializedName("total_points") val totalPoints: Int?,
    @SerializedName("is_guest") val isGuest: Boolean?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("last_login_at") val lastLoginAt: String?
)

data class UpdateProfileRequest(
    @SerializedName("displayName") val displayName: String? = null,
    @SerializedName("disabilityType") val disabilityType: String? = null,
    @SerializedName("disabilityOther") val disabilityOther: String? = null,
    @SerializedName("emergencyContact") val emergencyContact: String? = null,
    @SerializedName("language") val language: String? = null,
    @SerializedName("darkTheme") val darkTheme: String? = null,
    @SerializedName("fontSize") val fontSize: String? = null,
    @SerializedName("fontFamily") val fontFamily: String? = null,
    @SerializedName("speechRate") val speechRate: Double? = null
)

data class DisabilityTypeRequest(
    @SerializedName("disabilityType") val disabilityType: String
)

data class PasswordChangeRequest(
    @SerializedName("currentPassword") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String
)

data class FcmTokenRequest(
    @SerializedName("fcmToken") val fcmToken: String
)

data class GuardianModeRequest(
    @SerializedName("enabled") val enabled: Boolean
)

data class StatusResponse(
    @SerializedName("message") val message: String
)

data class UserResponse(
    @SerializedName("user") val user: UserDto
)
