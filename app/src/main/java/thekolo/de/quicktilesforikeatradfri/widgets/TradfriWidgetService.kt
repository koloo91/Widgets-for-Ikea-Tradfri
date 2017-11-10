package thekolo.de.quicktilesforikeatradfri.widgets

import android.content.Intent
import android.widget.RemoteViewsService

class TradfriWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return TradfriViewsFactory(applicationContext)
    }
}