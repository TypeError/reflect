package org.zaproxy.zap.extension.reflect

import org.parosproxy.paros.extension.ExtensionAdaptor
import org.parosproxy.paros.extension.ExtensionHook
import org.parosproxy.paros.network.HttpSender
import java.awt.EventQueue

class ExtensionReflect : ExtensionAdaptor(NAME) {

    private val reflectPanel = ReflectPanel()
    private val reflectListener = ReflectListener(reflectPanel)

    companion object {
        const val NAME = "Reflect"
    }

    override fun hook(extensionHook: ExtensionHook?) {
        super.hook(extensionHook)
        if (view != null) {
            HttpSender.addListener(reflectListener)
            extensionHook?.hookView?.addStatusPanel(reflectPanel)
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
                reflectPanel.setTabFocus()
            }
        }
    }
}

