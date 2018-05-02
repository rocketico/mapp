package io.rocketico.core

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigInteger

class EthereumHelperTest {
    val TEST_WALLET = "0x89292cf683fc405680333f2c8e57ae8cd366a2da"
    val ETH_NODE = "https://mainnet.infura.io/fd28b54b765d41c8d352d092576bb125"
    val EOS_CONTRACT = "0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0"
    val eh = EthereumHelper(ETH_NODE);

    @Test
    fun getBalance() {
        assertEquals(BigInteger.valueOf(4377414309999999778), eh.getBalance(TEST_WALLET));
    }

    @Test
    fun getBalanceErc20() {
        assertEquals(BigInteger.valueOf(155) * BigInteger.TEN.pow(18), eh.getBalanceErc20(EOS_CONTRACT, TEST_WALLET))
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