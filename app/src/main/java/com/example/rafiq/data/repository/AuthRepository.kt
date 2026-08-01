package com.example.rafiq.data.repository

import com.example.rafiq.data.local.TokenManager
import com.example.rafiq.data.local.UserPreferences
import com.example.rafiq.data.remote.api.AuthApi
import com.example.rafiq.data.remote.api.UserApi
import com.example.rafiq.data.remote.dto.*
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val userApi: UserApi,
    private val tokenManager: TokenManager,
    private val userPreferences: UserPreferences
) {
    sealed class Result<out T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val message: String, val code: String? = null) : Result<Nothing>()
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()!!
                tokenManager.saveToken(body.token, body.user.id, body.user.email)
                userPreferences.setLoggedIn(true)
                Result.Success(body)
            } else {
                parseError(response.errorBody()?.string(), "Login failed")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun register(email: String, password: String, displayName: String = ""): Result<AuthResponse> {
        return try {
            val response = authApi.register(RegisterRequest(email, password, displayName))
            if (response.isSuccessful) {
                val body = response.body()!!
                tokenManager.saveToken(body.token, body.user.id, body.user.email)
                userPreferences.setLoggedIn(true)
                Result.Success(body)
            } else {
                parseError(response.errorBody()?.string(), "Registration failed")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun guestLogin(): Result<AuthResponse> {
        return try {
            val response = authApi.guestLogin(GuestRequest(true))
            if (response.isSuccessful) {
                val body = response.body()!!
                tokenManager.saveToken(body.token, body.user.id, body.user.email)
                userPreferences.setLoggedIn(true)
                Result.Success(body)
            } else {
                Result.Error("Guest login failed")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun logout() {
        tokenManager.clearToken()
        userPreferences.setLoggedIn(false)
        userPreferences.setIntroCompleted(false)
        userPreferences.setWhatsNewVersion(0)
    }

    suspend fun getProfile(): Result<UserDto> {
        return try {
            val response = authApi.getMe()
            if (response.isSuccessful) {
                Result.Success(response.body()!!.user)
            } else {
                Result.Error("Failed to fetch profile")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun updateDisabilityType(type: String): Result<UserDto> {
        return try {
            val response = userApi.updateDisabilityType(DisabilityTypeRequest(type))
            if (response.isSuccessful) {
                Result.Success(response.body()!!.user)
            } else {
                Result.Error("Failed to update disability type")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<String> {
        return try {
            val response = authApi.changePassword(PasswordChangeRequest(currentPassword, newPassword))
            if (response.isSuccessful) {
                Result.Success(response.body()!!.message)
            } else {
                parseError(response.errorBody()?.string(), "Failed to change password")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun deleteAccount(): Result<String> {
        return try {
            val response = authApi.deleteAccount()
            if (response.isSuccessful) {
                tokenManager.clearToken()
                userPreferences.setLoggedIn(false)
                Result.Success("Account deleted")
            } else {
                Result.Error("Failed to delete account")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Result<UserDto> {
        return try {
            val response = userApi.updateProfile(request)
            if (response.isSuccessful) {
                Result.Success(response.body()!!.user)
            } else {
                Result.Error("Failed to update profile")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Network error")
        }
    }

    private fun parseError(errorBody: String?, fallback: String): Result.Error {
        val body = errorBody ?: return Result.Error(fallback)
        return try {
            val json = JSONObject(body)
            val message = json.optString("error").ifEmpty { fallback }
            val code = json.optString("code").ifEmpty { null }
            Result.Error(message, code)
        } catch (e: Exception) {
            Result.Error(if (body.isBlank()) fallback else body)
        }
    }
}
