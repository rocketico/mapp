package io.rocketico.core

import io.rocketico.core.model.response.TokenHistoryResponse
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetTransactionCount
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.util.*

class EthereumHelper(networkUrl: String) {
    private val web3 = Web3jFactory.build(HttpService(networkUrl))

    val GAS_PRICE = BigInteger.valueOf(41000000000L)
    val GAS_LIMIT = BigInteger.valueOf(21000L)

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

    fun getTokensHistory(tokenTypeList: List<String>, dateFrom: Date, dateTo: Date = Date()): List<TokenHistoryResponse>? {
        return Utils.api.getTokensHistory(dateFrom, dateTo, tokenTypeList).execute().body()
    }

    fun sendEth(credentials: Credentials, address: String, value: BigInteger, gasPrice: BigInteger = GAS_PRICE, gasLimit: BigInteger = GAS_LIMIT): String? {
        //get nonce
        val ethGetTransactionCount: EthGetTransactionCount = web3.ethGetTransactionCount(
                credentials.address, DefaultBlockParameterName.LATEST).send();
        val nonce: BigInteger = ethGetTransactionCount.transactionCount

        // create our transaction
        val rawTransaction: RawTransaction = RawTransaction.createEtherTransaction(
                nonce,
                gasPrice,
                gasLimit,
                address,
                value
        )

        // sign & send our transaction
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        val hexValue: String = Numeric.toHexString(signedMessage);
        val ethSendTransaction: EthSendTransaction = web3.ethSendRawTransaction(hexValue).send();
        return ethSendTransaction.transactionHash
    }
}