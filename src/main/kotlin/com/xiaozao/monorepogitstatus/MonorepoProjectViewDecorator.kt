package com.xiaozao.monorepogitstatus

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.openapi.project.Project
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.JBColor
import java.awt.Color

class MonorepoProjectViewDecorator(private val project: Project) : ProjectViewNodeDecorator {

    override fun decorate(node: ProjectViewNode<*>?, data: PresentationData?) {
        if (node == null || data == null) return
        if (node !is PsiDirectoryNode) return

        val virtualFile = node.virtualFile ?: return
        val projectBasePath = project.basePath ?: return

        val relativePath = getRelativePath(projectBasePath, virtualFile.path) ?: return

        val settings = MonorepoSettings.getInstance(project)
        val matchedConfig = settings.directories.find { config ->
            relativePath == config.path || relativePath.startsWith("${config.path}/")
        } ?: return

        if (relativePath != matchedConfig.path) return

        val service = project.getService(GitStatusService::class.java)
        val status = service.getStatusForDirectory(matchedConfig)

        val modCount = status.modified + status.untracked
        val addCount = status.added
        val delCount = status.deleted

        // 保存已有的文本片段
        val existingFragments = ArrayList(data.coloredText)

        // 如果没有文本片段，添加目录名
        if (existingFragments.isEmpty()) {
            val dirName = virtualFile.name
            data.addText(dirName, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        } else {
            // 重新添加已有的片段
            data.clearText()
            existingFragments.forEach { fragment ->
                data.addText(fragment.text, fragment.attributes)
            }
        }

        // 添加空格
        data.addText(" ", SimpleTextAttributes.REGULAR_ATTRIBUTES)

        // ~修改 - 蓝色
        val blueAttr = SimpleTextAttributes(
            SimpleTextAttributes.STYLE_PLAIN,
            JBColor(Color(89, 168, 252), Color(89, 168, 252))
        )
        data.addText("~$modCount", blueAttr)

        data.addText("/", SimpleTextAttributes.GRAYED_ATTRIBUTES)

        // +新增 - 红色
        val redAttr = SimpleTextAttributes(
            SimpleTextAttributes.STYLE_PLAIN,
            JBColor(Color(237, 106, 94), Color(237, 106, 94))
        )
        data.addText("+$addCount", redAttr)

        data.addText("/", SimpleTextAttributes.GRAYED_ATTRIBUTES)

        // -删除 - 灰色
        data.addText("-$delCount", SimpleTextAttributes.GRAYED_ATTRIBUTES)
    }

    private fun getRelativePath(basePath: String, fullPath: String): String? {
        val normalizedBase = basePath.trimEnd('/')
        val normalizedFull = fullPath.trimEnd('/')

        return if (normalizedFull.startsWith(normalizedBase)) {
            normalizedFull.substring(normalizedBase.length).trimStart('/')
        } else {
            null
        }
    }
}
