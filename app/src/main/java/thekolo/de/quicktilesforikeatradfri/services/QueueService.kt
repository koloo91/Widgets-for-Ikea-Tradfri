package thekolo.de.quicktilesforikeatradfri.services

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.runBlocking

typealias Action = () -> Job

class QueueService private constructor() {
    private val actions: MutableList<Action> = mutableListOf()

    fun addAction(action: Action) {
        actions.add(action)

        executeNextAction()
    }

    private fun executeNextAction() {
        if (actions.isEmpty()) return

        runBlocking {
            println("executeNextAction before")
            actions.first()().join()
            println("executeNextAction after")
            actions.removeAt(0)

            executeNextAction()
        }
    }

    companion object {
        private var instance: QueueService? = null

        fun instance(): QueueService {
            if (instance == null)
                instance = QueueService()

            return instance!!
        }
    }
}