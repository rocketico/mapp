package io.rocketico.core.model.response

enum class Tokens(private val value: String) {
    ETH("ETH");

    override fun toString(): String {
        return value
    }
}