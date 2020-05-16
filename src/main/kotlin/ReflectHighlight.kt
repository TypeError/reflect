package org.zaproxy.zap.extension.reflect

import org.parosproxy.paros.network.HttpMessage
import org.parosproxy.paros.view.View
import org.zaproxy.zap.extension.search.SearchMatch

class ReflectHighlight(
    private val reqRes: HttpMessage,
    private val headerTokens: List<String>,
    private val bodyTokens: List<String>
) {

    private val searchMatch: Pair<SearchMatch?, SearchMatch?> by lazy {
        Pair(searchMatcher(headerTokens), searchMatcher(bodyTokens))
    }

    fun highlight() {
        if (searchMatch.second != null) {
            View.getSingleton().responsePanel.highlightBody(searchMatch.second)
        } else if (searchMatch.first != null) {
            View.getSingleton().responsePanel.highlightHeader(searchMatch.first)
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
                matchResult.range.last + 1
            )
        }
    }
}
