package thekolo.de.quicktilesforikeatradfri.widgets

import android.content.Intent
import android.widget.RemoteViewsService

class DevicesiWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return DevicesViewsFactory(applicationContext)
    }
}