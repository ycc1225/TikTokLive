package com.example.tiktoklive.data.model

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("id") val id: String,
    @SerializedName("name") val userName: String,
    @SerializedName("avatar") val avatarUrl: String,
    @SerializedName("comment") val content: String,
    @SerializedName("createdAt") val createdAt: String? = null
)
