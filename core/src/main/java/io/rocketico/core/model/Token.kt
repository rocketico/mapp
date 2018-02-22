package io.rocketico.core.model

import java.io.Serializable
import java.util.*

data class Token(
        val address: String,
        var name: String?,
        var decimals: Int?,
        var balance: Float? = null,
        var balanceLastUpdate: Date? = Date(),
        val uuid: UUID = UUID.randomUUID()
) : Serializable