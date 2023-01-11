package blockchain

import kotlin.random.Random

abstract class Agent(
    protected val id: String,
    protected val blockchain: Blockchain
) : Thread() {

    protected val random = Random(id.hashCode())

    override fun run() {
        while (!isInterrupted) {
            val myBalance = blockchain.getBalance(id)
            // randomly sending 1/6 of my balance to 1..3 users
            if (myBalance / 6 > 0) {
                val amount = myBalance / 6
                repeat(random.nextInt(1, 4)) {
                    val to = allUserNames[random.nextInt(0, allUserNames.size)]
                    blockchain.submitTransaction(Transaction(id, to, amount))
                }
            }
            if (!doWork()) {
                break
            }
        }
    }

    abstract fun doWork(): Boolean

}