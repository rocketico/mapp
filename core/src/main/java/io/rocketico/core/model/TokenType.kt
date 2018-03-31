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

    //todo change description of tokens
    ETH("ETH",
            18,
            "",
            "July 30, 2015",
            "",
            "",
            "https://www.ethereum.org",
            0f,
            0f,
            "https://etherscan.io"),

    WABI("WABI",
            18,
            "0x286bda1413a2df81731d4930ce2f862a35a609fe",
            "",
            "",
            "",
            "https://wacoin.io",
            0f,
            0f,
            "https://etherscan.io"),

    TEST_KOVAN_TOKEN("TEST_KOVAN_TOKEN",
            18,
            "0x8f6a033f38a41cf12c4fe28ae7475187ab9884cc",
            "",
            "",
            "",
            "https://test-token.io",
            0f,
            0f,
            "https://kovan.etherscan.io");

    fun isEther(): Boolean {
        return codeName == TokenType.ETH.codeName
    }

    override fun toString(): String {
        return codeName
    }

}