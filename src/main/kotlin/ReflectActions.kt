package org.zaproxy.zap.extension.reflect

import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class ReflectionActions(
    private val panel: ReflectPanel
) : ActionListener {
    private val table = panel.table
    private val actionsMenu = JPopupMenu()
    private val copyURLs = JMenuItem("Copy URL(s)")
    private val deleteMenu = JMenuItem("Delete Reflection(s)")
    private val clearMenu = JMenuItem("Clear Reflections")

    init {
        copyURLs.addActionListener(this)
        deleteMenu.addActionListener(this)
        clearMenu.addActionListener(this)
        actionsMenu.add(copyURLs)
        actionsMenu.addSeparator()
        actionsMenu.add(deleteMenu)
        actionsMenu.add(clearMenu)
        panel.table.componentPopupMenu = actionsMenu
    }


    override fun actionPerformed(e: ActionEvent?) {
        if (table.selectedRow == -1) return
        val selectedReflections = getSelectedReflections()
        when (e?.source) {
            deleteMenu -> {
                panel.model.removeReflection(selectedReflections)
            }
            clearMenu -> {
                panel.model.clearReflections()
            }
            copyURLs -> {
                val urls = selectedReflections.map { it.url }.joinToString()
                val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
                clipboard.setContents(StringSelection(urls), null)
            }
        }
    }

    private fun getSelectedReflections(): MutableList<ReflectedResponse> {
        val selectedReflections: MutableList<ReflectedResponse> = ArrayList()
        for (index in table.selectedRows) {
            selectedReflections.add(panel.model.reflections[index])
        }
        return selectedReflections
    }
}