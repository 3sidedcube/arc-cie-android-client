package com.cube.arc.workflow.manager

import com.cube.arc.workflow.model.Module
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Manager class for loading data source into usable list of Modules for easy access
 */
object ModulesManager
{
	lateinit var modules: List<Module>

	fun init(dataSource: InputStream)
	{
		modules = Gson().fromJson(InputStreamReader(dataSource), object : TypeToken<ArrayList<Module>>(){}.type)
	}
}
