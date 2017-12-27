package thekolo.de.quicktilesforikeatradfri.services

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.runBlocking

typealias Action = () -> Job

class QueueService private constructor() {
    private val actions: MutableList<Action> = mutableListOf()
    private var retryCounter = 0

    fun addAction(action: Action) {
        actions.add(action)

        executeNextAction()
    }

    private fun executeNextAction() {
        if (actions.isEmpty()) return

        try {
            runBlocking {
                if(retryCounter >= MAX_RETRIES) {
                    actions.first()().join()
                }

                actions.removeAt(0)

                executeNextAction()
            }
        } catch (e: Exception) {
            retryCounter++
            e.printStackTrace()
        }
    }

    companion object {
        const val MAX_RETRIES = 5

        private var instance: QueueService? = null

        fun instance(): QueueService {
            if (instance == null)
                instance = QueueService()

            return instance!!
        }
    }
}