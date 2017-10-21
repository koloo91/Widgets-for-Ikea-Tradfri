package thekolo.de.widgetsforikeatradfri.widgets

import android.content.Intent
import android.widget.RemoteViewsService
import kotlinx.coroutines.experimental.runBlocking
import thekolo.de.widgetsforikeatradfri.Client

class TradfriWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        val devices = runBlocking {
            (Client.getInstance().getDevices().await() ?: emptyList())
        }

        return TradfriViewsFactory(applicationContext, devices)
    }
}