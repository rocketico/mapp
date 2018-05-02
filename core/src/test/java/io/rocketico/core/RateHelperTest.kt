package io.rocketico.core

import io.rocketico.core.model.TokenType
import org.junit.Test

import org.junit.Assert.*
import java.util.*

class RateHelperTest {

    @Test
    fun getTokenRateByDate() {
        val tmpRates = RateHelper.getTokenRateByDate(listOf(TokenType.ETH.codeName), Date(1525255200000L))
        assertEquals(1, tmpRates?.rates?.size)
    }

    @Test
    fun getTokenRatesByRange() {
    }

    @Test
    fun getYesterdayTokenRate() {
    }
}