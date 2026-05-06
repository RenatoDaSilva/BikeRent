package com.bike.rent

import java.util.Date

data class ParcelaResponse(
    val proximaParcela: Int,
    val total: Int,
    val proximoVlParcela: Double,
    val proximaDtPrevistaPgto: Date
)

data class ClientResponse(
    val nome: String,
    val parcelas: ParcelaResponse?
)
