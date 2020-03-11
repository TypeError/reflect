package org.zaproxy.zap.extension.reflect

import org.parosproxy.paros.extension.AbstractPanel
import org.parosproxy.paros.view.View
import java.awt.BorderLayout
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableRowSorter


class ReflectPanel : AbstractPanel() {
    val model = ReflectionsModel()
    val table = JTable(model)

    init {
        ReflectionActions(this, model.reflections)
        table.autoResizeMode = JTable.AUTO_RESIZE_OFF
        table.columnModel.getColumn(0).preferredWidth = 30 // ID
        table.columnModel.getColumn(1).preferredWidth = 50 // method
        table.columnModel.getColumn(2).preferredWidth = 120 // host
        table.columnModel.getColumn(3).preferredWidth = 450 // params
        table.columnModel.getColumn(4).preferredWidth = 250 // path
        table.columnModel.getColumn(5).preferredWidth = 150 // title
        table.columnModel.getColumn(6).preferredWidth = 145 // date
        table.columnModel.getColumn(7).preferredWidth = 50 // status
        table.columnModel.getColumn(8).preferredWidth = 50 // length
        table.columnModel.getColumn(9).preferredWidth = 100 // mime
        table.columnModel.getColumn(10).preferredWidth = 100 // protocol
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        table.rowSorter = TableRowSorter(model)

        table.selectionModel.addListSelectionListener {
            if (table.selectedRow != -1) {
                val displayedReflections = model.displayedReflections
                val selectedRow = table.convertRowIndexToModel(table.selectedRow)
                val requestResponse = displayedReflections[selectedRow].msg
                View.getSingleton().displayMessage(requestResponse)
            }
        }
        name = "Reflect"
        layout = BorderLayout()
        val reflectionsTable = JScrollPane(table)
        add(reflectionsTable)
    }

    fun addReflection(reflection: ReflectedResponse) {
        model.addReflection(reflection)
    }
}

class ReflectionsModel : AbstractTableModel() {
    private val columns =
        listOf(
            "ID",
            "Method",
            "Host",
            "Reflected Parameters",
            "Path",
            "Title",
            "Added",
            "Status",
            "Length",
            "MIME",
            "Protocol"
        )
    var reflections: MutableList<ReflectedResponse> = ArrayList()
    var displayedReflections: MutableList<ReflectedResponse> = ArrayList()
        private set

    override fun getRowCount(): Int = displayedReflections.size

    override fun getColumnCount(): Int = columns.size

    override fun getColumnName(column: Int): String {
        return columns[column]
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return when (columnIndex) {
            0 -> java.lang.Integer::class.java
            1 -> String::class.java
            2 -> String::class.java
            3 -> String::class.java
            4 -> String::class.java
            5 -> String::class.java
            6 -> String::class.java
            7 -> String::class.java
            8 -> String::class.java
            9 -> String::class.java
            10 -> String::class.java
            else -> throw RuntimeException()
        }
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val reflection = displayedReflections[rowIndex]

        return when (columnIndex) {
            0 -> rowIndex
            1 -> reflection.method
            2 -> reflection.host
            3 -> reflection.parameters
            4 -> reflection.path
            5 -> reflection.title
            6 -> reflection.dateTime
            7 -> reflection.statusCode.toString()
            8 -> reflection.length.toString()
            9 -> reflection.mimeType
            10 -> reflection.protocol
            else -> ""
        }
    }

    fun addReflection(reflection: ReflectedResponse) {
        this.reflections.add(reflection)
        displayedReflections = this.reflections
        fireTableRowsInserted(displayedReflections.lastIndex, displayedReflections.lastIndex)
    }

    fun removeReflection(reflection: MutableList<ReflectedResponse>) {
        this.reflections.removeAll(reflection)
        refreshReflections()
    }

    fun clearReflections() {
        reflections.clear()
        refreshReflections()
    }

    private fun refreshReflections(updatedReflection: MutableList<ReflectedResponse> = reflections) {
        displayedReflections = updatedReflection
        fireTableDataChanged()
    }
}
