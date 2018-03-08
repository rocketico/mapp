package io.rocketico.core.model

import io.rocketico.core.model.response.Tokens
import java.io.Serializable
import java.util.*

data class Token(
        val address: String,
        var name: Tokens?,
        var decimals: Int?,
        var rate: Float? = null,
        var balance: Float? = null,
        var balanceLastUpdate: Date? = Date(),
        val uuid: UUID = UUID.randomUUID()
) : Serializable {
    val isEther: Boolean
        get() = name.toString().toLowerCase() == "eth"

}