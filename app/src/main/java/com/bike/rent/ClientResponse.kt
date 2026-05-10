package com.bike.rent

data class MovimentoResponse(
    val parcela: String?,
    val dtPrevistaPgto: String?,
    val dtPagamento: String?,
    val vlParcela: String?,
    val multa: String?,
    val encargos: String?,
    val vlPago: String?,
    val vlPagoAcumulado: String?
)

data class ParcelaResponse(
    val total: String?,
    val pagas: String?,
    val pendentes: String?,
    val proximaParcela: String?,
    val proximaDtPrevistaPgto: String?,
    val proximoVlParcela: String?,
    val parcelaNoMes: String?,
    val parcelasNoMes: String?,
    val movimentos: List<MovimentoResponse>?
)

data class ClientResponse(
    val id: String?,
    val nome: String?,
    val telefone: String?,
    val email: String?,
    val cpf: String?,
    val loginHash: String?,
    val parcelas: ParcelaResponse?
)
