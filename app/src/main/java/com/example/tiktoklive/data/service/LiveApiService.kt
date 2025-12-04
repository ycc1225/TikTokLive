package com.example.tiktoklive.data.service

import com.example.tiktoklive.data.model.Comment
import com.example.tiktoklive.data.model.Host
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    companion object {
        private const val BASE_URL = "https://691ec8ffbb52a1db22bf1066.mockapi.io/"

        val api: LiveApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LiveApiService::class.java)
        }
    }
}