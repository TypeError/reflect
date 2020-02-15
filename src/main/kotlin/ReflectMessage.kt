package org.zaproxy.zap.extension.reflect

import org.zaproxy.zap.extension.httppanel.HttpPanelRequest
import org.zaproxy.zap.extension.httppanel.HttpPanelResponse
import org.zaproxy.zap.extension.httppanel.Message
import org.zaproxy.zap.view.HttpPanelManager


class ReflectMessage {
    private val httpPanelManager: HttpPanelManager = HttpPanelManager.getInstance()
    val requestPanel = HttpPanelRequest(false, "req")
    val responsePanel = HttpPanelResponse(false, "res")

    init {
        httpPanelManager.addRequestPanel(requestPanel)
        requestPanel.isEnableViewSelect = true
    }

    fun displayReqRes(msg: Message) {
        requestPanel.clearView(true)
        responsePanel.clearView(false)
        requestPanel.message = msg
        responsePanel.message = msg
        requestPanel.setTabFocus()
    }
}