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
    val method: String,
    val statusCode: Int,
    val title: String,
    val length: Int,
    val mimeType: String,
    val protocol: String,
    val parameters: String
)

class ReflectListener(private val reflectPanel: ReflectPanel) : HttpSenderListener {

    override fun onHttpResponseReceive(msg: HttpMessage?, initiator: Int, sender: HttpSender?) {
        msg?.let {
            if (msg.isInScope) {
                val params = mutableSetOf<HtmlParameter>()
                params.addAll(msg.urlParams)
                params.addAll(msg.formParams)
                val responseBody = msg.responseBody.toString()
                val reflected = params.asSequence().filter { it.value.length > 4 }
                    .filter { responseBody.contains(it.value, ignoreCase = true) }.toSet()
                if (reflected.isNotEmpty()) {
                    val now = LocalDateTime.now()
                    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val dateTime = now.format(dateFormatter) ?: ""
                    val parameters = params.joinToString { "${it.name}=${it.value}" }
                    val reqRes = ReflectedResponse(
                        msg = msg,
                        dateTime = dateTime,
                        host = msg.requestHeader?.uri?.host ?: "",
                        url = msg.requestHeader?.uri,
                        method = msg.requestHeader?.method ?: "",
                        statusCode = msg.responseHeader.statusCode,
                        title = getTitle(responseBody),
                        length = msg.responseHeader.contentLength,
                        mimeType = msg.responseHeader.getHeaderValues(HttpHeader.CONTENT_TYPE).toString(),
                        protocol = msg.requestHeader.uri.scheme,
                        parameters = parameters
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

