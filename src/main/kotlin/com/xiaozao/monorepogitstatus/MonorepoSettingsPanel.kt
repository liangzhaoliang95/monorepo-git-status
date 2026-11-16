package com.xiaozao.monorepogitstatus

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.JTextField
import javax.swing.SpinnerNumberModel
import javax.swing.table.AbstractTableModel

class MonorepoSettingsPanel {

    val panel: JPanel = JPanel(BorderLayout(0, 8))

    private val tableModel = DirectoryTableModel()
    private val table = JBTable(tableModel).apply {
        preferredScrollableViewportSize = Dimension(540, 200)
        columnModel.getColumn(0).preferredWidth = 160
        columnModel.getColumn(1).preferredWidth = 380
    }

    val refreshIntervalSpinner = JSpinner(SpinnerNumberModel(30, 5, 3600, 5))

    init {
        val decorator = ToolbarDecorator.createDecorator(table)
            .setAddAction { addDirectory() }
            .setRemoveAction { removeSelectedDirectory() }
            .setEditAction { editSelectedDirectory() }
            .createPanel()

        val bottomPanel = JPanel()
        bottomPanel.add(JLabel("Auto-refresh interval (seconds):"))
        bottomPanel.add(refreshIntervalSpinner)

        val descLabel = JLabel(
            "<html><p>Configure directories to monitor (relative paths from project root).</p>" +
            "<p>Example: <code>packages/app</code> or <code>modules/core</code></p></html>"
        )

        panel.add(descLabel, BorderLayout.NORTH)
        panel.add(decorator, BorderLayout.CENTER)
        panel.add(bottomPanel, BorderLayout.SOUTH)
    }

    private fun addDirectory() {
        val dialog = DirectoryEditDialog(null)
        if (dialog.showAndGet()) {
            dialog.getConfig()?.let { tableModel.addDirectory(it) }
        }
    }

    private fun removeSelectedDirectory() {
        val row = table.selectedRow
        if (row >= 0) tableModel.removeDirectory(row)
    }

    private fun editSelectedDirectory() {
        val row = table.selectedRow
        if (row >= 0) {
            val dialog = DirectoryEditDialog(tableModel.getDirectory(row))
            if (dialog.showAndGet()) {
                dialog.getConfig()?.let { tableModel.updateDirectory(row, it) }
            }
        }
    }

    fun getDirectories(): List<DirectoryConfig> = tableModel.getDirectories()

    fun setDirectories(dirs: List<DirectoryConfig>) = tableModel.setDirectories(dirs)

    fun getRefreshInterval(): Int = refreshIntervalSpinner.value as Int

    fun setRefreshInterval(interval: Int) {
        refreshIntervalSpinner.value = interval
    }

    private class DirectoryTableModel : AbstractTableModel() {
        private val columns = arrayOf("Display Name", "Directory Path")
        private val dirs = mutableListOf<DirectoryConfig>()

        override fun getRowCount(): Int = dirs.size
        override fun getColumnCount(): Int = 2
        override fun getColumnName(col: Int): String = columns[col]
        override fun isCellEditable(row: Int, col: Int): Boolean = false

        override fun getValueAt(row: Int, col: Int): Any = when (col) {
            0 -> dirs[row].displayName
            1 -> dirs[row].path
            else -> ""
        }

        fun addDirectory(config: DirectoryConfig) {
            dirs.add(config)
            fireTableRowsInserted(dirs.lastIndex, dirs.lastIndex)
        }

        fun removeDirectory(index: Int) {
            dirs.removeAt(index)
            fireTableRowsDeleted(index, index)
        }

        fun updateDirectory(index: Int, config: DirectoryConfig) {
            dirs[index] = config
            fireTableRowsUpdated(index, index)
        }

        fun getDirectory(index: Int): DirectoryConfig = dirs[index]

        fun getDirectories(): List<DirectoryConfig> = dirs.toList()

        fun setDirectories(list: List<DirectoryConfig>) {
            dirs.clear()
            dirs.addAll(list)
            fireTableDataChanged()
        }
    }
}

class DirectoryEditDialog(private val existing: DirectoryConfig?) : DialogWrapper(true) {

    private val displayNameField = JTextField(existing?.displayName ?: "", 22)
    private val pathField = JTextField(existing?.path ?: "", 32)

    init {
        title = if (existing == null) "Add Directory" else "Edit Directory"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val nameRow = JPanel()
        nameRow.add(JLabel("Display Name:"))
        nameRow.add(displayNameField)

        val pathRow = JPanel()
        pathRow.add(JLabel("Directory Path: "))
        pathRow.add(pathField)

        val hint = JLabel(
            "<html><small>Relative path from project root, e.g.: <code>packages/app</code></small></html>"
        )

        panel.add(nameRow)
        panel.add(pathRow)
        panel.add(hint)
        return panel
    }

    fun getConfig(): DirectoryConfig? {
        val path = pathField.text.trim()
        if (path.isEmpty()) return null
        val name = displayNameField.text.trim().ifBlank { path.substringAfterLast('/') }
        return DirectoryConfig(path = path, displayName = name)
    }
}
