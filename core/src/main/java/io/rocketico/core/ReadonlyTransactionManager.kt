package io.rocketico.core

import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.tx.TransactionManager
import java.io.IOException
import java.math.BigInteger

class ReadonlyTransactionManager(web3j: Web3j, fromAddress: String) : TransactionManager(web3j, fromAddress) {

    @Throws(IOException::class)
    override fun sendTransaction(
            gasPrice: BigInteger, gasLimit: BigInteger, to: String, data: String, value: BigInteger): EthSendTransaction {
        throw UnsupportedOperationException(
                "Only read operations are supported by this transaction manager")
    }
}