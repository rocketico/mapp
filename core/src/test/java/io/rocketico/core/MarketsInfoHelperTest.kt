package io.rocketico.core

import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import org.junit.Test

import org.junit.Assert.*

class MarketsInfoHelperTest {

    @Test
    fun getTokenInfoFromMarketsForEth() {
        val info = MarketsInfoHelper.getTokenInfoFromMarkets(TokenType.ETH.codeName, Currency.USD.codeName)

        assertTrue(info?.marketsInfo?.get(0)?.exchange != null)
        assertTrue(info?.marketsInfo?.get(0)?.highestRate24h != null)
        assertTrue(info?.marketsInfo?.get(0)?.lowestRate24h != null)
        assertTrue(info?.marketsInfo?.get(0)?.marketCapitalization != null)
        assertTrue(info?.marketsInfo?.get(0)?.traidingVolume24h != null)
    }

    @Test
    fun getTokenInfoFromMarketsForWaBi() {
        val info = MarketsInfoHelper.getTokenInfoFromMarkets(TokenType.WABI.codeName, Currency.USD.codeName)

        assertTrue(info?.marketsInfo?.get(0)?.exchange != null)
        assertTrue(info?.marketsInfo?.get(0)?.highestRate24h != null)
        assertTrue(info?.marketsInfo?.get(0)?.lowestRate24h != null)
        assertTrue(info?.marketsInfo?.get(0)?.marketCapitalization != null)
        assertTrue(info?.marketsInfo?.get(0)?.traidingVolume24h != null)
    }
}