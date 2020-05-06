package org.zaproxy.zap.extension.reflect

import org.apache.commons.httpclient.URI
import org.parosproxy.paros.network.HtmlParameter
import org.parosproxy.paros.network.HttpHeader
import org.parosproxy.paros.network.HttpMessage
import org.parosproxy.paros.network.HttpSender
import org.zaproxy.zap.network.HttpSenderListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ReflectedResponse(
    val msg: HttpMessage,
    val dateTime: String,
    val host: String,
    val url: URI?,
    val path: String,
    val method: String,
    val statusCode: Int,
    val title: String,
    val length: String,
    val mimeType: String,
    val protocol: String,
    val parameters: String,
    val highlighter: ReflectHighlight
)

class ReflectListener(private val reflectPanel: ReflectPanel) : HttpSenderListener {

    override fun onHttpResponseReceive(msg: HttpMessage?, initiator: Int, sender: HttpSender?) {
        msg?.let { req ->
            if (req.isInScope || reflectPanel.scopeCheckbox.isSelected) {
                val params = mutableSetOf<HtmlParameter>()
                params.addAll(req.urlParams)
                params.addAll(req.formParams)
                val responseHeader = req.requestHeader.toString()
                val responseBody = req.responseBody.toString()
                val reflected = params.asSequence().filter { it.value.length >= 4 }
                    .filter {
                        responseBody.contains(
                            it.value,
                            ignoreCase = true
                        )
                    }.toSet()
                val reflectedTokens = reflected.map { it.value }.toSet().toList()
                if (reflected.isNotEmpty()) {
                    val now = LocalDateTime.now()
                    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val dateTime = now.format(dateFormatter) ?: ""
                    val parameters = reflected.joinToString { "${it.name}=${it.value}" }
                    val length = if (req.responseHeader.contentLength != -1) {
                        req.responseHeader.contentLength.toString()
                    } else {
                        ""
                    }
                    val reqRes = ReflectedResponse(
                        msg = req,
                        dateTime = dateTime,
                        host = req.requestHeader?.uri?.host ?: "",
                        url = req.requestHeader?.uri,
                        path = req.requestHeader.uri.path,
                        method = req.requestHeader?.method ?: "",
                        statusCode = req.responseHeader.statusCode,
                        title = getTitle(responseBody),
                        length = length,
                        mimeType = req.responseHeader.getHeaderValues(HttpHeader.CONTENT_TYPE).toString(),
                        protocol = req.requestHeader.uri.scheme,
                        parameters = parameters,
                        highlighter = ReflectHighlight(req, reflectedTokens)
                    )
                    reflectPanel.addReflection(reqRes)
                }
            }
        }
    }

    private fun getTitle(responseBody: String): String {
        val titleRegex = "<title>(.*?)</title>".toRegex()
        val title = titleRegex.find(responseBody)?.value ?: ""
        return title.removePrefix("<title>").removeSuffix("</title>")
    }

    override fun getListenerOrder(): Int = 0

    override fun onHttpRequestSend(msg: HttpMessage?, initiator: Int, sender: HttpSender?) {}
}

