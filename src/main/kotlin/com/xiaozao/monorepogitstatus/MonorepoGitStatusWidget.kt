package com.xiaozao.monorepogitstatus

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidget.TextPresentation
import com.intellij.openapi.wm.impl.status.EditorBasedWidget
import com.intellij.util.Consumer
import java.awt.Component
import java.awt.event.MouseEvent
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class MonorepoGitStatusWidget(project: Project) : EditorBasedWidget(project), TextPresentation {

    @Volatile private var displayText = "Git Status"
    @Volatile private var tooltipText = "Monorepo Git Status"

    private val scheduler = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "MonorepoGitStatus-Refresher").apply { isDaemon = true }
    }
    @Volatile private var scheduledTask: ScheduledFuture<*>? = null

    override fun ID(): String = WIDGET_ID

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    override fun getText(): String = displayText

    override fun getTooltipText(): String = tooltipText

    override fun getAlignment(): Float = Component.CENTER_ALIGNMENT

    override fun getClickConsumer(): Consumer<MouseEvent>? = Consumer { refresh() }

    override fun install(statusBar: StatusBar) {
        super.install(statusBar)
        startScheduledRefresh()
        refresh()
    }

    override fun dispose() {
        scheduledTask?.cancel(true)
        scheduler.shutdownNow()
        super.dispose()
    }

    private fun startScheduledRefresh() {
        val settings = MonorepoSettings.getInstance(project)
        val interval = settings.refreshIntervalSeconds.toLong().coerceAtLeast(5L)
        scheduledTask?.cancel(false)
        scheduledTask = scheduler.scheduleAtFixedRate(
            { refresh() },
            interval,
            interval,
            TimeUnit.SECONDS
        )
    }

    fun refresh() {
        ApplicationManager.getApplication().executeOnPooledThread {
            val settings = MonorepoSettings.getInstance(project)
            val service = project.getService(GitStatusService::class.java)

            if (settings.directories.isEmpty()) {
                displayText = "GitStatus: No dirs"
                tooltipText = "Click to configure directories in Settings > Tools > Monorepo Git Status"
            } else {
                val statuses = service.getStatusForDirectories(settings.directories)
                if (statuses.isEmpty()) {
                    displayText = "GitStatus: N/A"
                    tooltipText = "Not a git repository or git not found"
                } else {
                    displayText = statuses.joinToString(" | ") { it.toStatusString() }
                    tooltipText = buildTooltip(statuses)
                }
            }
            myStatusBar?.updateWidget(WIDGET_ID)
        }
    }

    private fun buildTooltip(statuses: List<DirGitStatus>): String {
        return statuses.joinToString("\n\n") { status ->
            buildString {
                append("${status.displayName} (${status.path})")
                if (!status.hasChanges) {
                    append("\n  No changes")
                } else {
                    if (status.added > 0) append("\n  Added: ${status.added}")
                    if (status.modified > 0) append("\n  Modified: ${status.modified}")
                    if (status.deleted > 0) append("\n  Deleted: ${status.deleted}")
                    if (status.untracked > 0) append("\n  Untracked: ${status.untracked}")
                }
            }
        }
    }

    companion object {
        const val WIDGET_ID = "MonorepoGitStatusWidget"
    }
}
