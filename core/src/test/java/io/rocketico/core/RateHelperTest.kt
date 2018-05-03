package io.rocketico.core

import io.rocketico.core.model.TokenType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class RateHelperTest {

    @Test
    fun getTokenRateByDate() {
        val tmpRates = RateHelper.getTokenRateByDate(
                listOf(
                        TokenType.ETH.codeName,
                        TokenType.WABI.codeName
                ),
                Date(1525255200000) //GMT: Wednesday, May 2, 2018 10:00:00 AM
        )
        assertEquals(1, tmpRates?.rates?.size)
    }

    @Test
    fun getTokenRatesByRange() {
        val tmpRates = RateHelper.getTokenRatesByRange(listOf(TokenType.ETH.codeName), Date(1525255200000L))
        assertEquals(1, tmpRates?.rates?.size)
    }
}