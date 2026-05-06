package com.bike.rent

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("exec")
    suspend fun login(
        @Query("option") option: String = "login",
        @Query("cpf") cpf: String,
        @Query("hash") hash: String
    ): Response<LoginResponse>

    @GET("exec")
    suspend fun getClientInfo(
        @Query("option") option: String = "cliente",
        @Query("hash") hash: String
    ): Response<ClientResponse>
}
