package thekolo.de.quicktilesforikeatradfri.widgets.groups

import android.content.Intent
import android.widget.RemoteViewsService

class GroupsWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return GroupsViewsFactory(applicationContext)
    }
}