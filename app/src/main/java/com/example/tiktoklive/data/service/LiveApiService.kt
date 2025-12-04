package com.example.tiktoklive.data.service

import com.example.tiktoklive.data.model.Comment
import com.example.tiktoklive.data.model.Host
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LiveApiService {

    @GET("api/v1/hosts/{id}")
    suspend fun getHostInfo(@Path("id") id: String): Host

    @GET("api/v1/comments_5")
    suspend fun getComments(): List<Comment>

    @FormUrlEncoded
    @POST("api/v1/comments_5")
    suspend fun sendComment(@Field("comment") content: String): Comment
}