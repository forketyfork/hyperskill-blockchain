package blockchain

class Miner(id: String, blockchain: Blockchain) : Agent(id, blockchain) {

    override fun doWork(): Boolean {
        val messages = blockchain.getPendingTransactions()
        val prefix = "0".repeat(blockchain.zeroCount)
        val blockId = (blockchain.last?.id ?: 0) + 1
        var block: Block? = null
        while (block?.hash?.startsWith(prefix) != true) {
            if (isInterrupted) {
                return false
            }
            if (blockId < (blockchain.last?.id ?: 0) + 1) {
                // someone else has found the block
                return true
            }
            block = Block(
                id = blockId,
                minerId = id,
                prev = blockchain.last,
                magicNumber = random.nextInt(),
                transactions = listOf(Transaction(null, id, 100)) + messages
            )
        }
        blockchain.submitBlock(block)
        return true
    }

}
