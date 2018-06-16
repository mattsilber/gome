package com.guardanis.display

import com.guardanis.Device
import com.guardanis.Logger
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Insets
import java.awt.Toolkit
import java.io.PrintStream
import java.util.ArrayList

import javax.swing.DefaultListModel
import javax.swing.JFrame
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.ListModel
import javax.swing.ListSelectionModel
import javax.swing.WindowConstants

class DisplayController(private val ipAddresses: List<String>) {

    private var frame: JFrame? = null

    private var ipAddressesView: JTextArea? = null
    private var logView: JTextArea? = null

    private var listScroller: JScrollPane? = null
    private var deviceListView: JList<*>? = null
    private val deviceListModel = DefaultListModel<Device>()

    private var devices: List<Device>? = null
    private val logData = ArrayList<String>()

    fun show(devices: List<Device>): DisplayController {
        this.devices = devices
        this.frame = buildFrame()
        this.deviceListView = buildListView()

        this.ipAddressesView = JTextArea("IP Addresses: \n    ${ipAddresses.joinToString("\n    ")}")
        ipAddressesView?.margin = Insets(10, 12, 10, 12)

        frame?.add(ipAddressesView!!, BorderLayout.PAGE_START)

        devices.forEach({ deviceListModel.addElement(it) })

        this.listScroller = JScrollPane(deviceListView)
        listScroller?.preferredSize = Dimension(frame!!.width / 2, frame!!.height)

        frame?.add(listScroller!!, BorderLayout.LINE_START)

        this.logView = JTextArea("Log starting...")
        logView?.preferredSize = Dimension(frame!!.width / 2 - 5, frame!!.height)
        logView?.margin = Insets(10, 12, 10, 12)

        frame?.add(logView!!, BorderLayout.LINE_END)
        frame?.pack()

        Logger.addLogListener({ onLogValueAdded(it) })

        return this
    }

    fun onDeviceAdded(device: Device) {
        deviceListModel.addElement(device)

        frame?.revalidate()
    }

    fun onDeviceRemoved(device: Device) {
        deviceListModel.removeElement(device)

        frame?.revalidate()
    }

    private fun buildFrame(): JFrame {
        val frame = JFrame("gome")
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.isResizable = false
        frame.contentPane.layout = BorderLayout()

        val screenSize = Toolkit.getDefaultToolkit().screenSize

        frame.preferredSize = Dimension(
                (screenSize.getWidth() * .35).toInt(),
                (screenSize.getHeight() / 3).toInt())

        frame.pack()
        frame.isVisible = true

        return frame
    }

    private fun buildListView(): JList<*> {
        val list = JList(deviceListModel)
        list.selectionMode = ListSelectionModel.SINGLE_INTERVAL_SELECTION
        list.layoutOrientation = JList.VERTICAL
        list.cellRenderer = DeviceRenderer()

        return list
    }

    private fun onLogValueAdded(value: String) {
        java.awt.EventQueue.invokeLater {
            logData.add(value)

            while (18 < logData.size)
                logData.removeAt(logData.size - 1)

            logView?.text = logData.joinToString("\n")
        }
    }
}
