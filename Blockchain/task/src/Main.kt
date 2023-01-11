package blockchain

import kotlin.concurrent.withLock

fun main() {

    // create the blockchain, miners and clients
    val blockchain = Blockchain()
    val miners = minerNames.map { Miner(it, blockchain) }
    val clients = clientNames.map { Client(it, blockchain) }

    val agents = (clients as List<Thread>).plus(miners)

    // start the clients and the miners
    agents.forEach { it.start() }

    // wait until the blockchain signals that enough blocks are mined
    blockchain.lock.withLock {
        blockchain.enoughBlocksMined.await()
    }

    // stop the miners and the clients
    agents.forEach {
        it.interrupt()
        it.join()
    }
}
