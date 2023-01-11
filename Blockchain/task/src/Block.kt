package blockchain

import kotlin.math.abs

data class Transaction(val from: String?, val to: String, val amount: Int) {
    override fun toString() = if (from == null) "$to gets $amount VC" else "$from sent $amount VC to $to"
}

class Block(
    val id: Long,
    val minerId: String,
    val magicNumber: Int,
    val prev: Block? = null,
    val transactions: List<Transaction>
) {

    private val transactionsString = transactions.drop(1).joinToString("\n")

    private val timestamp: Long = System.currentTimeMillis()

    val hash: String =
        applySha256("$id|$minerId|$timestamp|$transactionsString|${prev?.hash ?: 0}|$magicNumber")

    var generationDuration = -1L
        set(value) {
            assert(field == -1L) { "Can't reassign generationDuration" }
            field = value
        }

    var zeroCountChange = -1
        set(value) {
            assert(field == -1) { "Can't reassign zeroCountChange" }
            field = value
        }

    private val zeroCountChangeDescription
        get() = when {
            zeroCountChange < 0 -> "decreased by ${abs(zeroCountChange)}"
            zeroCountChange > 0 -> "increased by $zeroCountChange"
            else -> "stays the same"
        }

    override fun toString() = """Block:
Created by: $minerId
${transactions.first()}
Id: $id
Timestamp: $timestamp
Magic number: $magicNumber
Hash of the previous block:
${prev?.hash ?: 0}
Hash of the block:
$hash
Block data: ${if (transactions.size == 1) "No transactions" else "\n" + transactionsString}
Block was generating for ${generationDuration / 1000} seconds
N $zeroCountChangeDescription
"""

}