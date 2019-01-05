package thekolo.de.quicktilesforikeatradfri.services

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

typealias Action = () -> Job

class QueueService private constructor() {

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

        currentJob = GlobalScope.launch {
            try {
                isExecutingAction = true
                actions.first()().join()

                actions.removeAt(0)
            } catch (e: Exception) {
                Log.e("QueueService", e.message)
            } finally {
                isExecutingAction = false
                executeNextAction()
            }
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