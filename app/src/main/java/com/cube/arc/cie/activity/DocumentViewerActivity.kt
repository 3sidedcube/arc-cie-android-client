package com.cube.arc.cie.activity;

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.View
import android.widget.*
import com.cube.arc.R
import com.cube.arc.cie.fragment.DownloadHelper
import com.cube.arc.workflow.manager.ExportManager
import com.cube.arc.workflow.model.Module
import com.cube.lib.helper.IntentDataHelper
import com.cube.lib.parser.URLImageParser
import com.cube.lib.util.bind
import com.cube.lib.util.mimeIcon
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

/**
 * Document viewer activity used for viewing content preview of a document
 */
class DocumentViewerActivity : AppCompatActivity()
{
	private val title by bind<TextView>(R.id.title)
	private val preview by bind<EditText>(R.id.preview)
	private val documentIcon by bind<ImageView>(R.id.mime_icon)
	private val documentTitle by bind<TextView>(R.id.document_title)
	private val documentSize by bind<TextView>(R.id.document_size)
	private val close by bind<View>(R.id.close)
	private val export by bind<Button>(R.id.export)
	private val downloadProgress by bind<ProgressBar>(R.id.download_progress)
	private lateinit var module: Module
	private lateinit var downloadTask: DownloadHelper

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setContentView(R.layout.document_viewer_activity_view)

		module = IntentDataHelper.retrieve(this::class.java)
		downloadTask = DownloadHelper.newInstance(this, "downloader")

		setUi()
	}

	override fun onSaveInstanceState(outState: Bundle?)
	{
		super.onSaveInstanceState(outState)
		IntentDataHelper.store(this::class.java, module)
	}

	fun setUi()
	{
		val files = module.attachments?.filter { file -> file.featured }

		if (files?.isEmpty() ?: true)
		{
			finish()
			return
		}

		val parser = Parser.builder().build()
		val document = parser.parse(module.content)
		val renderer = HtmlRenderer.builder().build()
		val htmlContent = renderer.render(document)

		preview.setText(Html.fromHtml(htmlContent, URLImageParser(preview), null))

		files?.let {
			title.text = files[0].title
			documentTitle.text = files[0].title
			documentSize.text = "%.2fMB".format(files[0].size.toDouble() / 1024.0 / 1024.0)
			documentIcon.setImageResource(files[0].mimeIcon())

			downloadProgress.isIndeterminate = true

			if (downloadTask.isDownloading.get())
			{
				export.isEnabled = false
				downloadProgress.visibility = View.VISIBLE
			}

			downloadTask.progressLambda = { progress ->
				export.isEnabled = false
				downloadProgress.visibility = View.VISIBLE
				downloadProgress.isIndeterminate = false
				downloadProgress.max = 100
				downloadProgress.progress = progress
				downloadProgress.invalidate()
			}

			downloadTask.callbackLambda = { success, filePath ->
				export.isEnabled = true
				downloadProgress.visibility = View.INVISIBLE

				if (success)
				{
					Toast.makeText(this@DocumentViewerActivity, "File downloaded successfully", Toast.LENGTH_SHORT).show()

					export.setText(R.string.document_button_open)

					ExportManager.registerFileManifest(downloadTask.file)
					ExportManager.open(downloadTask.file, this@DocumentViewerActivity)
				}
				else
				{
					Toast.makeText(this@DocumentViewerActivity, "There was a problem downloading the file", Toast.LENGTH_LONG).show()
				}
			}

			export.setText(if (ExportManager.isFileDownloaded(files[0])) R.string.document_button_open else R.string.document_button_export)
			export.setOnClickListener {
				if (ExportManager.isFileDownloaded(files[0]))
				{
					ExportManager.open(files[0], this)
				}
				else
				{
					export.isEnabled = false
					downloadProgress.visibility = View.VISIBLE
					downloadProgress.progress = 0

					downloadTask = downloadTask.attach(this@DocumentViewerActivity)
					downloadTask.file = files[0]
					downloadTask.execute()
				}
			}
		}

		close.setOnClickListener {
			finish()
		}
	}

	override fun finish()
	{
		super.finish()
		downloadTask.detach()
	}
}
