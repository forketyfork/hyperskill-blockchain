package blockchain

import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.math.log10

class Blockchain {

    companion object {
        const val MAX_BLOCKS = 15L
    }

    val lock = ReentrantLock()
    val enoughBlocksMined: Condition = lock.newCondition()

    var last: Block? = null
        private set
        get() = synchronized(lock) { field }

    var zeroCount: Int = 3
        private set
        get() = synchronized(lock) { field }

    private var lastGenerationTime: Long = System.currentTimeMillis()

    private val transactions = mutableListOf<Transaction>()

    fun getPendingTransactions() = lock.withLock { transactions.toList() }

    fun getBalance(name: String): Int {
        var balance = 0
        var block = last
        while (block != null) {
            block.transactions.forEach { tx ->
                if (tx.from == name) {
                    balance -= tx.amount
                }
                if (tx.to == name) {
                    balance += tx.amount
                }
            }
            block = block.prev
        }
        return balance
    }

    fun submitTransaction(transaction: Transaction) = lock.withLock {
        if (transaction.from == null || getBalance(transaction.from) < transaction.amount) {
            println("Invalid transaction ignored: $transaction")
        } else {
            transactions.add(transaction)
        }
    }

    fun submitBlock(block: Block) = lock.withLock {
        val minerTransaction = block.transactions[0]
        val otherTransactions = block.transactions.drop(1)
        // validate the block, if the block is invalid, just ignore it
        // with the current implementation we don't need to validate the hash, as it's calculated in the Block class
        if (block.id == (last?.id ?: 0) + 1
            && block.prev == last
            && block.hash.startsWith("0".repeat(zeroCount))
            && minerTransaction.let { it.amount == 100 && it.from == null && it.to == block.minerId }
            && transactions.subList(0, otherTransactions.size) == otherTransactions
        ) {
            // calculate the generation duration
            System.currentTimeMillis().let { generationTime ->
                block.generationDuration = generationTime - lastGenerationTime
                lastGenerationTime = generationTime
            }

            // make sure the time to generate the block stays within (100..1000) milliseconds
            // if not, increase/decrease it logarithmically
            block.zeroCountChange = (2 - log10(block.generationDuration.toDouble()).toInt())
                // make sure zeroCount always stays positive
                .coerceAtLeast(1 - zeroCount)
            zeroCount += block.zeroCountChange

            last = block

            println(block)

            repeat(otherTransactions.size) { transactions.removeFirst() }

            // if we've mined enough blocks, signal the termination condition
            if (block.id == MAX_BLOCKS) {
                enoughBlocksMined.signal()
            }
        }
    }

}