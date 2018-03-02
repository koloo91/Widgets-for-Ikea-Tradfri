package thekolo.de.quicktilesforikeatradfri.services

import android.util.Log
import kotlinx.coroutines.experimental.CoroutineExceptionHandler
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

typealias Action = () -> Job

class QueueService private constructor() {
    private val handler = CoroutineExceptionHandler { _, ex ->
        isExecutingAction = false
        Log.println(Log.ERROR, "QueueService", Log.getStackTraceString(ex))

        executeNextAction()
    }

    private val actions: MutableList<Action> = mutableListOf()

    private var isExecutingAction = false
    private var currentJob: Job? = null

    fun addAction(action: Action) {
        actions.add(action)

        executeNextAction()
    }

    fun clearQueue() {
        currentJob?.cancel()
        actions.clear()
        isExecutingAction = false
    }

    private fun executeNextAction() {
        if (actions.isEmpty() || isExecutingAction) return

        currentJob = launch(handler) {
            isExecutingAction = true
            actions.first()().join()

            actions.removeAt(0)
            isExecutingAction = false
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