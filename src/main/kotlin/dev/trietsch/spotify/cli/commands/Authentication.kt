package dev.trietsch.spotify.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.trietsch.spotify.cli.common.CliContext
import dev.trietsch.spotify.cli.common.CliContext.SPOTIFY_API
import dev.trietsch.spotify.cli.common.OAuthCallbackServer

class Authentication : CliktCommand(
    name = COMMAND,
    help = "Authenticate against Stream Machine.",
    printHelpOnEmptyArgs = true
) {
    companion object {
        internal const val COMMAND = "auth"
    }

    init {
        subcommands(
            Login()
        )
    }

    override fun run() = Unit
}

class Login : CliktCommand(
    name = COMMAND,
    help = "Login to get a valid access token"
) {
    companion object {
        internal const val COMMAND = "login"
    }

    override fun run() {
        val url = SPOTIFY_API.authorizationCodeUri()
            .scope(
                listOf(
                    "app-remote-control",
                    "user-read-playback-position",
                    "user-read-playback-state",
                    "user-modify-playback-state",
                    "user-read-currently-playing",
                    "user-read-recently-played",
                    "user-top-read",
                    "user-read-private",
                    "user-read-email",
                    "ugc-image-upload"
                ).joinToString(" ")
            )
            .build()

        println("Authenticating, please navigate to: ${url.uri}")
        OAuthCallbackServer.waitForCallback(url.uri)

        println("Requesting token")
        val clientCredentials = SPOTIFY_API
            .authorizationCode(CliContext.OAuthCredentialsProvider.oAuthCallbackCode)
            .build()
            .execute()

        println("Setting access token")
        SPOTIFY_API.accessToken = clientCredentials.accessToken

        SPOTIFY_API.startResumeUsersPlayback().device_id("3491db6461abce7434d0a6f19f3393e956705774").build().execute()
    }
}
