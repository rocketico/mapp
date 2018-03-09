package io.rocketico.core.model

import java.io.Serializable
import java.util.*

data class Token(
        val address: String,
        var type: TokenType,
        var rate: Float? = null,
        var balance: Float? = null,
        var balanceLastUpdate: Date? = Date(),
        val uuid: UUID = UUID.randomUUID()
) : Serializable {
    val isEther: Boolean
        get() = type == TokenType.ETH

}