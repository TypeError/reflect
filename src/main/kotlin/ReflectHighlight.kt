package org.zaproxy.zap.extension.reflect

import org.parosproxy.paros.network.HttpMessage
import org.parosproxy.paros.view.View
import org.zaproxy.zap.extension.search.SearchMatch
import java.net.URLDecoder

class ReflectHighlight(
    private val reqRes: HttpMessage,
    private val headerTokens: List<String>,
    private val bodyTokens: List<String>
) {

    private val searchMatch: Pair<SearchMatch?, SearchMatch?> by lazy {
        Pair(
            searchMatcher(headerTokens, SearchMatch.Location.RESPONSE_HEAD),
            searchMatcher(bodyTokens, SearchMatch.Location.RESPONSE_BODY)
        )
    }

    fun highlight() {
        if (searchMatch.second != null) {
            View.getSingleton().responsePanel.highlightBody(searchMatch.second)
        } else if (searchMatch.first != null) {
            View.getSingleton().responsePanel.highlightHeader(searchMatch.first)
        }
    }

    private fun searchMatcher(tokens: List<String>, searchLocation: SearchMatch.Location): SearchMatch? {
        val token = tokens.minByOrNull { it.length } ?: return null
        val pattern = token.toRegex()
        var match = if (searchLocation == SearchMatch.Location.RESPONSE_BODY) {
            pattern.find(reqRes.responseBody.toString())
        } else {
            pattern.find(reqRes.responseHeader.toString())
        }

        if (match == null) {
            val decodedPattern = URLDecoder.decode(token, "UTF-8").toRegex()
            match = if (searchLocation == SearchMatch.Location.RESPONSE_BODY) {
                decodedPattern.find(reqRes.responseBody.toString())
            } else {
                decodedPattern.find(reqRes.responseHeader.toString())
            }
        }

        return match?.let { matchResult ->
            SearchMatch(
                reqRes,
                searchLocation,
                matchResult.range.first,
                matchResult.range.last + 1
            )
        }
    }
}
