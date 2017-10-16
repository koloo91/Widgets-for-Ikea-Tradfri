package thekolo.de.widgetsforikeatradfri

import android.content.Intent
import android.widget.RemoteViewsService
import kotlinx.coroutines.experimental.runBlocking

class TradfriWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        val devices = runBlocking {
            Client.getInstance().getDevices().await().filter { it != null }.map { it!! }
        }

        return TradfriViewsFactory(applicationContext, devices)
    }
}