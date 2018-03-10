package io.rocketico.core.model

enum class TokenType(val codeName: String, val decimals: Int, val contractAddress: String) {
    ETH("ETH", 18, ""),
    TEST("AED", 18, "0x8f6a033f38a41cf12c4fe28ae7475187ab9884cc"); //todo test token. Remove me

    override fun toString(): String {
        return codeName
    }

}