package org.zaproxy.zap.extension.reflect

import org.parosproxy.paros.extension.ExtensionAdaptor
import org.parosproxy.paros.extension.ExtensionHook
import org.parosproxy.paros.network.HttpSender
import java.awt.EventQueue

class ExtensionReflect : ExtensionAdaptor(NAME) {

    private var reflectPanel: ReflectPanel? = null
    private var reflectListener: ReflectListener? = null

    companion object {
        const val NAME = "Reflect"
    }

    override fun hook(extensionHook: ExtensionHook?) {
        super.hook(extensionHook)
        if (view != null) {
            reflectPanel = ReflectPanel()
            reflectPanel?.let { panel ->
                reflectListener = ReflectListener(panel)
                HttpSender.addListener(reflectListener)
                extensionHook?.hookView?.addStatusPanel(panel)
            }
        }
    }

    override fun unload() {
        HttpSender.removeListener(reflectListener)
    }

    override fun canUnload(): Boolean = true

    override fun getAuthor(): String = "Caleb Kinney"

    override fun getDescription(): String = "Finds reflected parameters."

    override fun postInstall() {
        super.postInstall()

        if (view != null) {
            EventQueue.invokeLater {
                reflectPanel?.setTabFocus()
            }
        }
    }
}

