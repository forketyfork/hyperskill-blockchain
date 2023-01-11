package blockchain

class Client(id: String, blockchain: Blockchain) : Agent(id, blockchain) {

    override fun doWork(): Boolean {
        return try {
            sleep(random.nextLong(200L, 300L))
            true
        } catch (e: InterruptedException) {
            false
        }
    }

}
