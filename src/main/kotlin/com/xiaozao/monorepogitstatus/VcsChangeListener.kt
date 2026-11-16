package com.xiaozao.monorepogitstatus

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.ChangeListListener
import com.intellij.ide.projectView.ProjectView

class VcsChangeListener(private val project: Project) : ChangeListListener {

    override fun changeListUpdateDone() {
        refreshProjectView()
    }

    private fun refreshProjectView() {
        // 刷新项目视图以更新装饰器
        ProjectView.getInstance(project).refresh()
    }
}
