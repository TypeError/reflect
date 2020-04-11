package org.zaproxy.zap.extension.reflect

import org.parosproxy.paros.network.HttpMessage
import org.parosproxy.paros.view.View
import org.zaproxy.zap.extension.search.SearchMatch

class ReflectHighlight(private val reqRes: HttpMessage, private val tokens: List<String>) {

    private val searchMatch: SearchMatch? by lazy {
        searchMatcher(tokens)
    }

    fun highlight() {
        searchMatch?.let {
            View.getSingleton().responsePanel.highlightBody(it)
        }
    }

    private fun searchMatcher(tokens: List<String>): SearchMatch? {
        val token = tokens.minBy { it.length } ?: return null
        val pattern = token.toRegex()
        val match = pattern.find(reqRes.responseBody.toString())

        return match?.let { matchResult ->
            SearchMatch(
                reqRes,
                SearchMatch.Location.RESPONSE_BODY,
                matchResult.range.first,
                matchResult.range.last
            )
        }
    }
}
