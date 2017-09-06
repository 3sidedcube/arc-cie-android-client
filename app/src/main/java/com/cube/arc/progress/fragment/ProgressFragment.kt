package com.cube.arc.progress.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.cube.arc.R
import com.cube.arc.progress.adapter.ProgressDirectoryAdapter
import com.cube.arc.workflow.manager.DirectoriesManager
import com.cube.lib.util.bind
import com.cube.lib.util.inflate

/**
 * Fragment for progress UI of directories
 */
class ProgressFragment : Fragment()
{
	private val title by bind<TextView>(R.id.progress_title)
	private val summary by bind<TextView>(R.id.progress_summary)
	private val progress by bind<ProgressBar>(R.id.progress)
	private val recyclerView by bind<RecyclerView>(R.id.recycler_view)
	private val adapter = ProgressDirectoryAdapter()

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? = container?.inflate(R.layout.progress_fragment_view)

	override fun onActivityCreated(savedInstanceState: Bundle?)
	{
		super.onActivityCreated(savedInstanceState)

		adapter.items = DirectoriesManager.directories

		recyclerView.layoutManager = LinearLayoutManager(activity)
		recyclerView.adapter = adapter

		val totalTools = DirectoriesManager.directories.fold(0, { acc, directory -> acc + DirectoriesManager.toolCount(directory, true) })
		val toolsCompleted = DirectoriesManager.directories.fold(0, { acc, directory -> acc + DirectoriesManager.completedToolCount(context, directory, true) })
		val progressPercent = Math.round((toolsCompleted.toDouble() / totalTools.toDouble()) * 100)

		summary.text = resources.getString(R.string.progress_summary, progressPercent.toString())
		progress.progress = progressPercent.toInt()
	}
}
