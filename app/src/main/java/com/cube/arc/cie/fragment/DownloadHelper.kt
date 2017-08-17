package com.cube.arc.cie.fragment

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.cube.arc.workflow.manager.ExportManager
import com.cube.arc.workflow.model.FileDescriptor
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Headless fragment used for holding the network request and updating the UI for the attached activity
 */
class DownloadHelper : Fragment()
{
	companion object
	{
		public fun newInstance(activity: AppCompatActivity, tagStr: String = "", file: FileDescriptor? = null): DownloadHelper
		{
			val helper = DownloadHelper().apply {
				this.tagStr = tagStr

				if (file != null)
				{
					this.file = file
				}
			}

			return helper.attach(activity)
		}
	}

	public lateinit var file: FileDescriptor
	public val isDownloading: AtomicBoolean = AtomicBoolean(false)
	private var tagStr: String = ""
	private var downloadTask: AsyncTask<Void, Int, Boolean>? = null
	public var progressLambda: ((Int) -> Unit)? = null
	public var callbackLambda: ((Boolean, File) -> Unit)? = null

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		retainInstance = true
	}

	/**
	 * Attaches the fragment to the given activity, or returns its attached instance if already attached
	 */
	fun attach(activity:AppCompatActivity): DownloadHelper
	{
		if (activity.supportFragmentManager.findFragmentByTag(tagStr) == null)
		{
			activity.supportFragmentManager.beginTransaction()
				.add(this@DownloadHelper, tagStr)
				.commit()

			return this
		}
		else
		{
			return activity.supportFragmentManager.findFragmentByTag(tagStr) as DownloadHelper
		}
	}

	/**
	 * Detaches the fragment from the current added activity, this will cancel any download task
	 */
	fun detach()
	{
		if (isAdded && activity != null)
		{
			progressLambda = null
			callbackLambda = null

			downloadTask?.cancel(true)
			isDownloading.set(false)

			activity.supportFragmentManager.beginTransaction()
				.remove(this@DownloadHelper)
				.commit()
		}
	}

	/**
	 * Executes the download task and updates the UI within [DocumentViewerActivity], can only be called once
	 * during a download.
	 */
	fun execute()
	{
		if (isDownloading.get()) return

		downloadTask = ExportManager.download(
			file = file,
			progress = { progress ->
				progressLambda?.also { callback ->
					isDownloading.set(true)

					if (isAdded)
					{
						callback.invoke(progress)
					}
				}
			},
			callback = { success, filePath ->
				callbackLambda?.also { callback ->
					isDownloading.set(false)

					if (isAdded)
					{
						callback.invoke(success, filePath)
					}
				}
			}
		)
	}
}
