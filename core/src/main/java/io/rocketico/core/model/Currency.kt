package io.rocketico.core.model

enum class Currency(val codeName: String, val currencySymbol: String) {
    USD("USD", "$"),
    TEST("TEST", "T"); //todo Debug. remove

    override fun toString(): String {
        return codeName
    }
}