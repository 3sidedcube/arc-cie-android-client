package com.cube.arc.cie

import android.app.Application
import android.os.Environment
import com.cube.arc.workflow.manager.ModulesManager
import com.cube.arc.workflow.manager.SearchManager
import java.io.File
import java.io.FileInputStream

/**
 * Application singleton for instantiating application configuration and data files
 */
class MainApplication : Application()
{
	companion object
	{
		public lateinit var BASE_PATH: File
	}

	override fun onCreate()
	{
		super.onCreate()

		// initialise module manager
		BASE_PATH = File(Environment.getExternalStorageDirectory().absoluteFile, "CIE-Documents")
		initManagers()
	}

	/**
	 * Initialises the manager classes
	 */
	fun initManagers()
	{
		var modulesStream = resources.assets.open("modules.json")
		val cacheModules = File(filesDir, "modules.json")
		if (cacheModules.exists())
		{
			modulesStream = FileInputStream(cacheModules)
		}

		ModulesManager.init(modulesStream)
		SearchManager.init(this)
	}
}
