package org.zaproxy.zap.extension.reflect

import org.parosproxy.paros.extension.ExtensionAdaptor
import org.parosproxy.paros.extension.ExtensionHook
import org.parosproxy.paros.network.HttpSender

class ExtensionReflect : ExtensionAdaptor(NAME) {

    companion object {
        const val NAME = "Reflect"
    }

    override fun hook(extensionHook: ExtensionHook?) {
        super.hook(extensionHook)
        val reflectPanel = ReflectPanel()
        HttpSender.addListener(ReflectListener(reflectPanel))
        extensionHook?.hookView?.addWorkPanel(reflectPanel)
    }

    override fun canUnload(): Boolean = true

    override fun getAuthor(): String = "Caleb Kinney"

    override fun getDescription(): String = "Finds reflected parameters."
}

