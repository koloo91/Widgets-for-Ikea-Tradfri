package thekolo.de.quicktilesforikeatradfri.widgets

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.app.job.JobScheduler
import android.app.job.JobInfo
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.util.Log
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.widgets.devices.DevicesAppWidgetProvider
import thekolo.de.quicktilesforikeatradfri.widgets.groups.GroupsAppWidgetProvider


class UpdateJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(LogName, "onStartJob")

        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)

        val devicesComponent = ComponentName(applicationContext, DevicesAppWidgetProvider::class.java)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(devicesComponent), R.id.devices_list_view)

        val groupsComponent = ComponentName(applicationContext, GroupsAppWidgetProvider::class.java)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(groupsComponent), R.id.groups_list_view)

        schedule(applicationContext)

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    companion object {
        const val LogName = "UpdateJobService"

        fun schedule(context: Context) {
            Log.d(LogName, "schedule")

            val serviceComponent = ComponentName(context, UpdateJobService::class.java)
            val builder = JobInfo.Builder(808, serviceComponent)
            builder.setMinimumLatency((20 * 1000L))
            builder.setOverrideDeadline(3ß * 1000L)
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)

            val jobScheduler = context.getSystemService(JobScheduler::class.java)
            jobScheduler.schedule(builder.build())
        }
    }
}