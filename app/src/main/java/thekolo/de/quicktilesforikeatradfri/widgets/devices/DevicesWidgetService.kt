package thekolo.de.quicktilesforikeatradfri.widgets.devices

import android.content.Intent
import android.widget.RemoteViewsService

class DevicesWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return DevicesViewsFactory(applicationContext)
    }
}