package com.xiaozao.monorepogitstatus

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

data class DirectoryConfig(
    var path: String = "",
    var displayName: String = ""
)

@State(
    name = "MonorepoGitStatusSettings",
    storages = [Storage("monorepo-git-status.xml")]
)
@Service(Service.Level.PROJECT)
class MonorepoSettings : PersistentStateComponent<MonorepoSettings.State> {

    data class State(
        var directories: MutableList<DirectoryConfig> = mutableListOf(),
        var refreshIntervalSeconds: Int = 30
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    var directories: MutableList<DirectoryConfig>
        get() = myState.directories
        set(value) {
            myState.directories = value
        }

    var refreshIntervalSeconds: Int
        get() = myState.refreshIntervalSeconds
        set(value) {
            myState.refreshIntervalSeconds = value
        }

    companion object {
        fun getInstance(project: Project): MonorepoSettings = project.service()
    }
}
