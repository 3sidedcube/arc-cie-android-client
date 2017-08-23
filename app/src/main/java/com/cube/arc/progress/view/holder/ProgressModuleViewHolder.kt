package com.cube.arc.progress.view.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.cube.arc.R
import com.cube.arc.workflow.manager.ModulesManager
import com.cube.arc.workflow.model.Module
import com.cube.lib.util.tint

/**
 * View holder for displaying the UI of the module progress
 */
class ProgressModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
	private val name = itemView.findViewById(R.id.module_name) as TextView
	private val hierarchy = itemView.findViewById(R.id.module_hierarchy) as TextView
	private val stepsProgress = itemView.findViewById(R.id.module_substeps_progress_text) as TextView
	private val toolsProgress = itemView.findViewById(R.id.module_critical_progress_text) as TextView
	private val totalProgress = itemView.findViewById(R.id.progress_text) as TextView
	private val moduleProgress = itemView.findViewById(R.id.module_progress) as ProgressBar
	private val subStepsDivider = itemView.findViewById(R.id.substeps_divider) as View
	private val criticalDivider = itemView.findViewById(R.id.critical_divider) as View

	fun populate(model: Module)
	{
		hierarchy.background.tint(hierarchy.resources.getColor(ModulesManager.moduleColours[model.order] ?: R.color.module_1))
		moduleProgress.tint(ModulesManager.moduleColours[model.order] ?: R.color.module_1)
		subStepsDivider.tint(ModulesManager.moduleColours[model.order] ?: R.color.module_1)
		criticalDivider.tint(ModulesManager.moduleColours[model.order] ?: R.color.module_1)

		val completedSubSteps = ModulesManager.completedSubStepCount(itemView.context, model)
		val totalSubSteps = ModulesManager.subStepCount(model)
		stepsProgress.text = itemView.resources.getString(R.string.progress_substep_summary, completedSubSteps.toString(), totalSubSteps.toString())

		val completedTools = ModulesManager.completedToolCount(itemView.context, model, true)
		val totalTools = ModulesManager.toolCount(model, true)
		toolsProgress.text = itemView.resources.getString(R.string.progress_tool_summary, completedTools.toString(), totalTools.toString())

		val totalComplete = Math.round((completedTools.toDouble() / totalTools.toDouble()) * 100.0).toInt()
		totalProgress.text = itemView.resources.getString(R.string.progress_text, totalComplete.toString())

		moduleProgress.progress = totalComplete
		name.text = model.title
		hierarchy.text = model.order.toString()
	}
}
