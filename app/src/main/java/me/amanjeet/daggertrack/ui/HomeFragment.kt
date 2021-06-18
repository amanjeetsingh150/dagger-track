package me.amanjeet.daggertrack.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import me.amanjeet.daggertrack.HeavyDependencyOne
import me.amanjeet.daggertrack.HeavyDependencyTwo
import me.amanjeet.daggertrack.R
import me.amanjeet.daggertrack.model.LogMessage
import me.amanjeet.daggertrack.ui.adapter.LogsAdapter
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.regex.Pattern
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var heavyDependencyOne: HeavyDependencyOne

    @Inject
    lateinit var heavyDependencyTwo: HeavyDependencyTwo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()
        try {
            daggerTrackLogsRecyclerView.layoutManager = LinearLayoutManager(activity)
            val daggerTrackLogs = extractDaggerTrackLog()
            val logsAdapter = LogsAdapter()
            daggerTrackLogsRecyclerView.adapter = logsAdapter
            logsAdapter.submitList(daggerTrackLogs)
        } catch (e: IOException) {
            Log.d("DaggerTrack", "${e.message}")
        }
    }

    private fun extractDaggerTrackLog(): List<LogMessage> {
        val daggerTrackLogList = mutableListOf<LogMessage>()
        val process = Runtime.getRuntime().exec("logcat -d -s DaggerTrack")
        val bufferedReader = BufferedReader(
            InputStreamReader(process.inputStream)
        )
        val rawLogs = StringBuilder()
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            rawLogs.append(line)
        }
        val matcher = Pattern.compile(REGEX_DAGGER_TRACK_LOGS).matcher(rawLogs.toString())
        val daggerTrackLogs = StringBuilder()
        while (matcher.find()) {
            matcher.group(0)?.let { daggerTrackLogs.append(it) }
            daggerTrackLogs.append("\n")
            daggerTrackLogList.add(LogMessage(daggerTrackLogs.toString()))
        }
        return daggerTrackLogList.toList()
    }

    companion object {
        private const val REGEX_DAGGER_TRACK_LOGS =
            "DaggerTrack: Total\\s(Off CPU|On CPU) time of me.amanjeet.daggertrack.(ui.HomeActivity: \\d|ui.HomeFragment: \\d\\d\\d\\d|DaggerTrackApp:\\s\\d)"
    }
}