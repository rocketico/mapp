package io.rocketico.core.model

import java.io.Serializable

enum class TokenType(val codeName: String,
                     val decimals: Int,
                     val contractAddress: String,
                     val launchDate: String,
                     val hashAlgorithm: String,
                     val networkPower: String,
                     val officialSite: String,
                     val available: Float,
                     val support: Float,
                     val blockChain: String) : Serializable {

    ETH("ETH",
            18,
            "",
            "July 30, 2015",
            "Ethash",
            "Try Cloud Mining",
            "ethereumclassic.org",
            99.5f,
            230.0f,
            "gastracker.io");

    fun isEther(): Boolean {
        return codeName == TokenType.ETH.codeName
    }

    override fun toString(): String {
        return codeName
    }

}