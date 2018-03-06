package io.rocketico.core

import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import java.math.BigInteger

class EthereumHelper(networkUrl: String) {
    val web3 = Web3jFactory.build(HttpService(networkUrl))

    fun getBalance(address: String): BigInteger {
        return web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get().balance
    }
}