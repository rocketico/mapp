package io.rocketico.core

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigInteger

class EthereumHelperTest {
    val TEST_WALLET = "0x89292cf683fc405680333f2c8e57ae8cd366a2da"
    val ETH_NODE = "https://mainnet.infura.io/fd28b54b765d41c8d352d092576bb125"
    val eh = EthereumHelper(ETH_NODE);

    @Test
    fun getBalance() {
        assertEquals(BigInteger.valueOf(4377414309999999778), eh.getBalance(TEST_WALLET));
    }

    @Test
    fun getBalanceErc20() {
    }

    @Test
    fun getTokensHistory() {
    }

    @Test
    fun sendEth() {
    }

    @Test
    fun sendErc20() {
    }
}