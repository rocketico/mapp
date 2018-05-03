package io.rocketico.core

import io.rocketico.core.model.TokenType
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class RateHelperTest {

    @Test
    fun getTokenRateByDateNormal() {
        val tmpRates = RateHelper.getTokenRateByDate(
                listOf(
                        TokenType.ETH.codeName,
                        TokenType.WABI.codeName
                ),
                Date(1525255200000) //GMT: Wednesday, May 2, 2018 10:00:00 AM
        )
        assertTrue(tmpRates?.rates?.size!! >= 1)
    }

    @Test
    fun getTokenRateByDateWithEmptyTokenTypeList() {
        val tmpRates = RateHelper.getTokenRateByDate(
                listOf(),
                Date(1525255200000) //GMT: Wednesday, May 2, 2018 10:00:00 AM
        )
        assertEquals(0, tmpRates?.rates?.size!!)
    }

    @Test
    fun getTokenRateByDateWithETH() {
        val tmpRates = RateHelper.getTokenRateByDate(
                listOf(TokenType.ETH.codeName),
                Date(1525255200000) //GMT: Wednesday, May 2, 2018 10:00:00 AM
        )
        assertTrue(tmpRates?.rates?.find {
            it.tokenSymbol.toLowerCase() == TokenType.ETH.codeName.toLowerCase()
        } != null)

        assertTrue(tmpRates?.rates?.size!! == 1)
    }

    @Test
    fun getTokenRateByDateWithWaBi() {
        val tmpRates = RateHelper.getTokenRateByDate(
                listOf(TokenType.WABI.codeName),
                Date(1525255200000) //GMT: Wednesday, May 2, 2018 10:00:00 AM
        )
        assertTrue(tmpRates?.rates?.find {
            it.tokenSymbol.toLowerCase() == TokenType.WABI.codeName.toLowerCase()
        } != null)

        assertTrue(tmpRates?.rates?.size!! == 2)
    }

    @Test
    fun getTokenRateByDateWithFutureDate() {
        val tmpRates = RateHelper.getTokenRateByDate(
                listOf(
                        TokenType.ETH.codeName,
                        TokenType.WABI.codeName
                ),
                Date(1563184800000) //GMT: Mon, 15 Jul 2019 10:00:00 AM
        )
        assertTrue(tmpRates?.rates?.find {
            it.tokenSymbol.toLowerCase() == TokenType.ETH.codeName.toLowerCase()
        } != null)

        assertTrue(tmpRates?.rates?.find {
            it.tokenSymbol.toLowerCase() == TokenType.WABI.codeName.toLowerCase()
        } != null)
    }

    @Test
    fun getTokenRateByDateWithDateWhenEthDoesNotExist() {
        val tmpRates = RateHelper.getTokenRateByDate(
                listOf(
                        TokenType.ETH.codeName,
                        TokenType.WABI.codeName
                ),
                Date(1265277600000) //GMT: Thu, 04 Feb 2010 10:00:00 AM
        )
        assertTrue(tmpRates?.rates?.find {
            it.tokenSymbol.toLowerCase() == TokenType.ETH.codeName.toLowerCase()
        } == null)

        assertTrue(tmpRates?.rates?.find {
            it.tokenSymbol.toLowerCase() == TokenType.WABI.codeName.toLowerCase()
        } == null)
    }

    @Test
    fun getTokenRateByDateWithDateWhenWaBiDoesNotExist() {
        val tmpRates = RateHelper.getTokenRateByDate(
                listOf(
                        TokenType.ETH.codeName,
                        TokenType.WABI.codeName
                ),
                Date(1460541600000) //GMT: Wed, 13 Apr 2016 10:00:00 AM
        )
        assertTrue(tmpRates?.rates?.find {
            it.tokenSymbol.toLowerCase() == TokenType.ETH.codeName.toLowerCase()
        } != null)

        assertTrue(tmpRates?.rates?.find {
            it.tokenSymbol.toLowerCase() == TokenType.WABI.codeName.toLowerCase()
        } == null)
    }

    @Test
    fun getTokenRatesByRange() {
        val tmpRates = RateHelper.getTokenRatesByRange(
                listOf(TokenType.ETH.codeName),
                Date(1525255200000) // May 2, 2018 10:00:00 AM GMT
        )
        assertTrue(tmpRates?.rates?.size!! >= 1)
    }
}