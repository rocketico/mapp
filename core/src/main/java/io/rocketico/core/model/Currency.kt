package io.rocketico.core.model

import java.io.Serializable

enum class Currency(val codeName: String, val currencySymbol: String): Serializable {
    USD("USD", "$");

    override fun toString(): String {
        return codeName
    }

    companion object {
        fun currencyFromString(curr: String): Currency? {
            return Currency.values().find { it.codeName == curr }
        }
    }
}