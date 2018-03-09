package io.rocketico.core.model

enum class TokenType(val codeName: String, val decimals: Int) {
    ETH("ETH", 18);

    override fun toString(): String {
        return codeName
    }

}