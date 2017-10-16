package thekolo.de.widgetsforikeatradfri

import android.content.Intent
import android.widget.RemoteViewsService

class TradfriWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return TradfriViewsFactory(applicationContext, intent!!)
    }
}