package org.zaproxy.zap.extension.reflect

import java.awt.FlowLayout
import javax.swing.*

class ReflectOptions(
    private val reflectPanel: ReflectPanel
) {
    val panel = JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
    private val loadPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
    private val searchBar = JTextField("", 20)
    private val searchPanel = JPanel(FlowLayout(FlowLayout.LEFT))

    init {
        val clearButton = JButton("Clear Reflections")
        val searchLabel = JLabel("Search Reflections:")
        val searchButton = JButton("Search")
        val resetButton = JButton("Reset")
        clearButton.addActionListener { clearReflection() }
        searchBar.addActionListener { searchReflection() }
        searchButton.addActionListener { searchReflection() }
        resetButton.addActionListener { resetSearch() }
        searchPanel.add(searchLabel)
        searchPanel.add(searchBar)
        searchPanel.add(searchButton)
        searchPanel.add(resetButton)
        loadPanel.add(clearButton)
        panel.leftComponent = searchPanel
        panel.rightComponent = loadPanel
        panel.dividerSize = 0
    }

    private fun searchReflection() {
        SwingUtilities.invokeLater {
            val searchText = searchBar.text.toLowerCase()
            var filteredReflection = this.reflectPanel.reflections
            if (searchText.isNotEmpty()) {
                filteredReflection = filteredReflection
                    .filter {
                        it.url.toString().toLowerCase().contains(searchText) ||
                                it.msg.requestBody.toString().toLowerCase().contains(
                                    searchText
                                ) ||
                                it.msg.responseBody.toString().toLowerCase().contains(
                                    searchText
                                )
                    }.toMutableList()
            }
            reflectPanel.model.refreshReflections(filteredReflection)
            rowSelection()
        }
    }

    private fun resetSearch() {
        searchBar.text = ""
        reflectPanel.model.refreshReflections()
        rowSelection()
    }

    private fun clearReflection() {
        reflectPanel.model.clearReflections()
    }

    private fun rowSelection() {
        val rowCount = reflectPanel.table.rowCount
        if (rowCount != -1) {
            reflectPanel.table.setRowSelectionInterval(rowCount - 1, rowCount - 1)
        }
    }
}