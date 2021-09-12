package me.amanjeet.daggertrack

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri

class DaggerTrackInstaller : ContentProvider() {

    override fun onCreate(): Boolean {
        val application = context!!.applicationContext as Application
        val applicationInfo = application.packageManager.getApplicationInfo(
            application.packageName,
            PackageManager.GET_META_DATA
        )
        val bundle = applicationInfo.metaData
        val minWallClockTime = bundle.getInt(MIN_WALL_CLOCK_TIME, 0)
        val minOnCpuTime = bundle.getInt(MIN_ON_CPU_TIME, 0)
        val minOffCpuTime = bundle.getInt(MIN_OFF_CPU_TIME, 0)
        val loggerType = DaggerTrack.LoggerType.TRACKER_ACTIVITY
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    companion object {
        private const val MIN_WALL_CLOCK_TIME = "me.amanjeet.daggertrack.MinWallClockTime"
        private const val MIN_ON_CPU_TIME = "me.amanjeet.daggertrack.MinOnCpuTime"
        private const val MIN_OFF_CPU_TIME = "me.amanjeet.daggertrack.MinOffCpuTime"
    }
}