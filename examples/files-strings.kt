package files

import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JScrollPane
import java.awt.BorderLayout
import java.io.File

fun renderChildren(f : File) : String {
    val sb = StringBuilder()
    fun String.add() {
        sb.append(this)
    }

    "<html>".add()
    """<table border="1">""".add()
    "<tr><td>Files in ${f.getCanonicalFile()?.getName()}</td><td># children</td></tr>".add()
    for (child in f.listFiles()) {
        """
            <tr>
                <td align="right">${child!!.getName()}</td>
                <td>${child!!.listFiles()?.size}</td>
            </tr>
        """.add()
    }
    "</table>".add()
    return sb.toString()!!
}


fun main(args : Array<String>) {
    val frame = JFrame("Files")
    frame.setSize(800, 600)
    val scrollPane = JScrollPane(JLabel(
        renderChildren(File("."))
    ))
    frame.add(scrollPane)


    frame.show()
}