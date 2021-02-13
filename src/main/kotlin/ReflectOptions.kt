package org.zaproxy.zap.extension.reflect

import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.JTextField
import javax.swing.SwingUtilities

class ReflectOptions(
    private val reflectPanel: ReflectPanel
) {
    val panel = JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
    private val loadPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
    private val searchBar = JTextField("", 20)
    private val searchPanel = JPanel(FlowLayout(FlowLayout.LEFT))
    val scopeCheckBox = JCheckBox("Collect out of scope requests")

    init {
        val clearButton = JButton("Clear Reflections")
        val searchLabel = JLabel("Search Reflections:")
        val searchButton = JButton("Search")
        val resetButton = JButton("Reset")
        clearButton.addActionListener { clearReflection() }
        searchBar.addActionListener { searchReflection() }
        searchButton.addActionListener { searchReflection() }
        resetButton.addActionListener { resetSearch() }
        loadPanel.add(clearButton)
        searchPanel.apply {
            add(searchLabel)
            add(searchBar)
            add(searchButton)
            add(resetButton)
            add(clearButton)
        }
        panel.leftComponent = searchPanel
        panel.rightComponent = loadPanel
        panel.dividerSize = 0
    }

    fun filtered(): Boolean {
        return if (searchBar.text.isNotEmpty()) {
            searchReflection()
            true
        } else {
            false
        }
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
        }
    }

    private fun resetSearch() {
        searchBar.text = ""
        reflectPanel.model.refreshReflections()
    }

    private fun clearReflection() {
        reflectPanel.model.clearReflections()
    }
}
