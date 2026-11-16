package com.xiaozao.monorepogitstatus

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class MonorepoStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        // 初始化预设扫描规则
        initializeDefaultDirectories(project)

        // 启动定期刷新任务
        ProjectViewRefreshService.getInstance(project).startRefresh()
    }

    private fun initializeDefaultDirectories(project: Project) {
        val settings = MonorepoSettings.getInstance(project)
        val projectBasePath = project.basePath ?: return
        val presetParents = listOf("packages", "apps", "services")
        val newDirs = mutableListOf<DirectoryConfig>()

        // 对于每个预设父目录，扫描其下的子目录
        for (parentDirName in presetParents) {
            val parentDir = java.io.File("$projectBasePath/$parentDirName")

            if (!parentDir.exists() || !parentDir.isDirectory) continue

            // 列出所有子目录
            val subDirs = parentDir.listFiles { file -> file.isDirectory }

            if (subDirs != null && subDirs.isNotEmpty()) {
                subDirs.forEach { subDir ->
                    val relativePath = "$parentDirName/${subDir.name}"
                    newDirs.add(DirectoryConfig(
                        path = relativePath,
                        displayName = subDir.name
                    ))
                }
            }
        }

        if (newDirs.isEmpty()) return

        // 合并并去重：以 path 为准
        val existingPaths = settings.directories.map { it.path }.toSet()
        val toAdd = newDirs.filter { it.path !in existingPaths }

        if (toAdd.isNotEmpty()) {
            settings.directories.addAll(toAdd)
        }
    }
}

class ProjectViewRefreshService(private val project: Project) {

    private val scheduler = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "MonorepoProjectView-Refresher").apply { isDaemon = true }
    }

    @Volatile
    private var scheduledTask: ScheduledFuture<*>? = null

    fun startRefresh() {
        val settings = MonorepoSettings.getInstance(project)
        if (settings.directories.isEmpty()) return

        val interval = settings.refreshIntervalSeconds.toLong().coerceAtLeast(5L)

        scheduledTask?.cancel(false)
        scheduledTask = scheduler.scheduleAtFixedRate(
            { refreshProjectView() },
            interval,
            interval,
            TimeUnit.SECONDS
        )
    }

    fun stopRefresh() {
        scheduledTask?.cancel(true)
    }

    private fun refreshProjectView() {
        ApplicationManager.getApplication().invokeLater {
            if (project.isDisposed) return@invokeLater

            // 刷新项目视图
            ProjectView.getInstance(project)?.refresh()
        }
    }

    fun dispose() {
        stopRefresh()
        scheduler.shutdownNow()
    }

    companion object {
        fun getInstance(project: Project): ProjectViewRefreshService {
            return project.getService(ProjectViewRefreshService::class.java)
        }
    }
}
