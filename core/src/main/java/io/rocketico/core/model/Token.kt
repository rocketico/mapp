package io.rocketico.core.model

import java.io.Serializable
import java.util.*

data class Token(
        var type: TokenType,
        var balanceLastUpdate: Date? = Date(),
        val uuid: UUID = UUID.randomUUID()
) : Serializable {
    val isEther: Boolean
        get() = type == TokenType.ETH

}