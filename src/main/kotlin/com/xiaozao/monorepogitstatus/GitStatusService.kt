package com.xiaozao.monorepogitstatus

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import java.io.File

data class DirGitStatus(
    val displayName: String,
    val path: String,
    val added: Int,
    val modified: Int,
    val deleted: Int,
    val untracked: Int
) {
    val hasChanges: Boolean get() = added + modified + deleted + untracked > 0

    fun toStatusString(): String {
        if (!hasChanges) return "$displayName ✓"
        val parts = mutableListOf<String>()
        if (added > 0) parts.add("+$added")
        if (modified > 0) parts.add("~$modified")
        if (deleted > 0) parts.add("-$deleted")
        if (untracked > 0) parts.add("?$untracked")
        return "$displayName: ${parts.joinToString(" ")}"
    }
}

@Service(Service.Level.PROJECT)
class GitStatusService(private val project: Project) {

    private val log = logger<GitStatusService>()

    fun getStatusForDirectories(configs: List<DirectoryConfig>): List<DirGitStatus> {
        val projectBasePath = project.basePath ?: return emptyList()
        return try {
            val gitLines = runGitStatus(projectBasePath) ?: return emptyList()
            configs.map { config -> parseStatusForDir(config, gitLines) }
        } catch (e: Exception) {
            log.warn("Failed to get git status", e)
            emptyList()
        }
    }

    fun getStatusForDirectory(config: DirectoryConfig): DirGitStatus {
        val projectBasePath = project.basePath ?: return createEmptyStatus(config)
        return try {
            val gitLines = runGitStatus(projectBasePath) ?: return createEmptyStatus(config)
            parseStatusForDir(config, gitLines)
        } catch (e: Exception) {
            log.warn("Failed to get git status for ${config.path}", e)
            createEmptyStatus(config)
        }
    }

    private fun createEmptyStatus(config: DirectoryConfig): DirGitStatus {
        val displayName = config.displayName.ifBlank {
            config.path.substringAfterLast('/').ifBlank { config.path }
        }
        return DirGitStatus(displayName, config.path, 0, 0, 0, 0)
    }

    private fun runGitStatus(workDir: String): List<String>? {
        return try {
            val process = ProcessBuilder("git", "status", "--porcelain", "-u")
                .directory(File(workDir))
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readLines()
            val exitCode = process.waitFor()
            if (exitCode == 0) output else null
        } catch (e: Exception) {
            log.warn("Failed to run git status in $workDir", e)
            null
        }
    }

    private fun parseStatusForDir(
        config: DirectoryConfig,
        gitLines: List<String>
    ): DirGitStatus {
        val configPath = config.path.trimEnd('/')
        var added = 0
        var modified = 0
        var deleted = 0
        var untracked = 0

        for (line in gitLines) {
            if (line.length < 4) continue
            val xy = line.substring(0, 2)
            val filePath = line.substring(3).trim()

            // 重命名格式: "oldpath -> newpath", 取新路径
            val actualPath = if (filePath.contains(" -> ")) {
                filePath.substringAfter(" -> ")
            } else {
                filePath
            }.trimStart('/')

            val matchPrefix = if (configPath.isEmpty()) "" else "$configPath/"
            if (matchPrefix.isNotEmpty() && !actualPath.startsWith(matchPrefix)) continue

            when {
                xy == "??" -> untracked++
                xy[0] == 'D' || xy[1] == 'D' -> deleted++
                xy[0] == 'A' -> added++
                xy[0] == 'R' || xy[1] == 'R' -> modified++
                xy[0] == 'M' || xy[1] == 'M' -> modified++
                else -> modified++
            }
        }

        val displayName = config.displayName.ifBlank {
            configPath.substringAfterLast('/').ifBlank { configPath }
        }
        return DirGitStatus(displayName, configPath, added, modified, deleted, untracked)
    }
}
