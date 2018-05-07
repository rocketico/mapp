package io.rocketico.core

import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigInteger

class WalletSendTest {

    private val ETH_NODE = "https://kovan.infura.io/fd28b54b765d41c8d352d092576bb125"
    private val walletAddress = "0x86738731A0cbFB07E103ae235403037346eA602e"
    private val tokenContractAddress = "0x8f6a033f38a41cf12c4fe28ae7475187ab9884cc"
    private val privateKey: String = System.getenv("PR_KEY")
    private val oneGwei = 1000000000L

    @Test
    fun sendEthTest() {
        val result = EthereumHelper(ETH_NODE).sendEth(privateKey, walletAddress,
                BigInteger.valueOf(oneGwei))
        println(result)

        assertTrue(result != null)
    }

    @Test
    fun sendTokenTest() {
        val result = EthereumHelper(ETH_NODE).sendErc20(privateKey, tokenContractAddress,
                walletAddress, BigInteger.valueOf(oneGwei))

        assertTrue(result?.transactionHash != null)
    }
}