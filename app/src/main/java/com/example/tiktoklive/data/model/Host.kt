package com.example.tiktoklive.data.model

import com.google.gson.annotations.SerializedName

data class Host(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatarUrl: String,
    @SerializedName("roomName") val roomName: String,
    @SerializedName("followerNum") val followerCount: Int,
    @SerializedName("createdAt") val createdAt: String
)
