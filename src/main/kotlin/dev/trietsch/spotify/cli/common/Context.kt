package dev.trietsch.spotify.cli.common

import com.github.ajalt.mordant.TermColors
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager

/**
 * Context for the CLI to operate in. Purpose is to hold data that is easily retrieved throughout the whole CLI
 */
object CliContext {
    object Terminal {
        internal val TERM_COLORS = TermColors()

        internal val SPOTIFY_GREEN = TERM_COLORS.rgb("1DB954")
    }

    object OAuthCredentialsProvider {
        var oAuthCallbackCode: String? = null
    }

    internal var VERBOSE_LOGGING = false
    internal val VERSION = CliContext.javaClass.`package`.implementationVersion ?: "snapshot"

    internal val SPOTIFY_API: SpotifyApi = SpotifyApi.Builder()
        .setClientId("91442357014c432589261382ca24c1ff")
        .setClientSecret("2fb9e910b69240efaf8255ba3c2139d4")
        .setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:8080/cli"))
        .build()
}
