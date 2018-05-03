package io.rocketico.core

import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import org.junit.Test

import org.junit.Assert.*

class MarketsInfoHelperTest {

    @Test
    fun getTokenInfoFromMarkets() {
        println(MarketsInfoHelper.getTokenInfoFromMarkets(TokenType.ETH.codeName, Currency.USD.codeName))
    }
}