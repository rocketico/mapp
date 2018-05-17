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
        assertTrue(tmpRates?.rates?.size!! == 2)
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

        assertTrue(tmpRates?.rates?.size!! == 1)
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

        assertTrue(tmpRates?.rates?.size == 0)
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

        assertTrue(tmpRates?.rates?.size == 1)
    }

    @Test
    fun getTokenRatesByRangeNormal() {
        val tmpRates = RateHelper.getTokenRatesByRange(
                listOf(TokenType.ETH.codeName),
                Date(1525255200000), // May 2, 2018 10:00:00 AM GMT
                Date(1525691216000) // Mon, 07 May 2018 11:06:56 AM
        )
        assertTrue(tmpRates?.rates?.size!! == 11)
    }

    @Test
    fun getTokenRatesByRangeWithWaBi() {
        val tmpRates = RateHelper.getTokenRatesByRange(
                listOf(TokenType.WABI.codeName),
                Date(1525255200000), // May 2, 2018 10:00:00 AM GMT
                Date(1525691216000) // Mon, 07 May 2018 11:06:56 AM
        )

        assertTrue(tmpRates?.rates?.size!! == 11)
    }

    @Test
    fun getTokenRatesByRangeWithFutureDate() {
        val tmpRates = RateHelper.getTokenRatesByRange(
                listOf(TokenType.WABI.codeName),
                Date(1563184800000), //GMT: Mon, 15 Jul 2019 10:00:00 AM
                Date(1585702800000)  //GMT: Wed, 01 Apr 2020 01:00:00 AM
        )

        assertTrue(tmpRates?.rates?.size!! == 1)
    }

    @Test
    fun getTokenRatesByRangeWithDateWhenEthDoesNotExist() {
        val tmpRates = RateHelper.getTokenRatesByRange(
                listOf(TokenType.ETH.codeName,
                        TokenType.WABI.codeName),
                Date(1265277600000), //GMT: Thu, 04 Feb 2010 10:00:00 AM
                Date(1351818000000)  //GMT: Fri, 02 Nov 2012 01:00:00 AM
        )

        assertTrue(tmpRates?.rates?.size!! == 0)
    }

    @Test
    fun getTokenRatesByRangeWithDateWhenWaBiDoesNotExist() {
        val tmpRates = RateHelper.getTokenRatesByRange(
                listOf(TokenType.WABI.codeName),
                Date(1265277600000), //GMT: Thu, 04 Feb 2010 10:00:00 AM
                Date(1460541600000) //GMT: Wed, 13 Apr 2016 10:00:00 AM
        )

        assertTrue(tmpRates?.rates?.size!! == 0)
    }

    @Test
    fun getTokenRatesByRangeWithReversedDates() {
        val tmpRates = RateHelper.getTokenRatesByRange(
                listOf(TokenType.WABI.codeName),
                Date(1525255200000), // May 2, 2018 10:00:00 AM GMT
                Date(1460541600000) //GMT: Wed, 13 Apr 2016 10:00:00 AM
        )

        assertTrue(tmpRates?.rates?.size!! == 0)
    }
}