package com.example.rafiq.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ContactDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("created_at") val createdAt: String?
)

data class ContactsResponse(
    @SerializedName("contacts") val contacts: List<ContactDto>
)

data class ContactResponse(
    @SerializedName("contact") val contact: ContactDto
)

data class CreateContactRequest(
    @SerializedName("name") val name: String,
    @SerializedName("phoneNumber") val phoneNumber: String
)
