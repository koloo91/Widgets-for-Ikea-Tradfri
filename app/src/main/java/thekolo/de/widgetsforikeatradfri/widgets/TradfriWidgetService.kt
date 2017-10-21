package thekolo.de.widgetsforikeatradfri.widgets

import android.content.Intent
import android.widget.RemoteViewsService
import kotlinx.coroutines.experimental.runBlocking
import thekolo.de.widgetsforikeatradfri.TradfriClient

class TradfriWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        val devices = runBlocking {
            //TODO: change this
            (TradfriClient.getInstance(applicationContext).getDevices().await() ?: emptyList())
        }

        return TradfriViewsFactory(applicationContext, devices)
    }
}