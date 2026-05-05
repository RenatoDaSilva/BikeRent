package com.bike.rent

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("?option=login")
    suspend fun login(
        @Query("cpf") cpf: String,
        @Query("hash") hash: String
    ): Response<LoginResponse>
}
