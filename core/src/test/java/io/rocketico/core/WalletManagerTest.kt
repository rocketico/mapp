package io.rocketico.core

import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class WalletManagerTest {
    val TEST_WALLET = "0x89292cf683fc405680333f2c8e57ae8cd366a2da"

    @Test
    fun getWalletTokensTest() {
        val response = WalletManager.getWalletTokens(TEST_WALLET)

        assertTrue(response?.size!! >= 1)
    }
}