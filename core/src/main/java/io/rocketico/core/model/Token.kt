package io.rocketico.core.model

import java.io.Serializable
import java.util.*

data class Token(
        val address: String,
        var name: String?,
        var decimals: Int?,
        var balance: Float? = null,
        var balanceLastUpdate: Date? = Date(),
        var rate: Rate? = null,
        val uuid: UUID = UUID.randomUUID()
) : Serializable