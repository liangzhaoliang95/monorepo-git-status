package com.xiaozao.monorepogitstatus

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class MonorepoSettingsConfigurable(private val project: Project) : Configurable {

    private var panel: MonorepoSettingsPanel? = null

    override fun getDisplayName(): String = "Monorepo Git Status"

    override fun createComponent(): JComponent {
        val settingsPanel = MonorepoSettingsPanel()
        panel = settingsPanel
        val settings = MonorepoSettings.getInstance(project)
        settingsPanel.setDirectories(settings.directories.map { it.copy() })
        settingsPanel.setRefreshInterval(settings.refreshIntervalSeconds)
        return settingsPanel.panel
    }

    override fun isModified(): Boolean {
        val p = panel ?: return false
        val settings = MonorepoSettings.getInstance(project)
        return p.getDirectories() != settings.directories ||
               p.getRefreshInterval() != settings.refreshIntervalSeconds
    }

    override fun apply() {
        val p = panel ?: return
        val settings = MonorepoSettings.getInstance(project)
        settings.directories = p.getDirectories().toMutableList()
        settings.refreshIntervalSeconds = p.getRefreshInterval()
    }

    override fun reset() {
        val p = panel ?: return
        val settings = MonorepoSettings.getInstance(project)
        p.setDirectories(settings.directories.map { it.copy() })
        p.setRefreshInterval(settings.refreshIntervalSeconds)
    }

    override fun disposeUIResources() {
        panel = null
    }
}
