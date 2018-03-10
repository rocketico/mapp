package io.rocketico.core

import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import java.math.BigInteger

class EthereumHelper(networkUrl: String) {
    private val web3 = Web3jFactory.build(HttpService(networkUrl))

    fun getBalance(address: String): BigInteger {
        return web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get().balance
    }

    fun getBalanceErc20(contractAddress: String, address: String, privateKey: String): BigInteger {
        val token = DetailedERC20.load(
                contractAddress,
                web3,
                Credentials.create(privateKey),
                BigInteger.ZERO,
                BigInteger.ZERO
        )
        return token.balanceOf(address).sendAsync().get()
    }
}