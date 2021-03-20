package dev.trietsch.spotify.cli

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.versionOption
import dev.trietsch.spotify.cli.Spot.Companion.COMMAND
import dev.trietsch.spotify.cli.commands.Authentication
import dev.trietsch.spotify.cli.common.CliContext
import dev.trietsch.spotify.cli.common.ColorHelpFormatter


//fun main(args: Array<String>) {
//    BrowserUtil.openUrlInBrowser("https://trietsch.dev")
//}
//    val spotifyApi: SpotifyApi = SpotifyApi.Builder()
//        .setClientId("91442357014c432589261382ca24c1ff")
//        .setClientSecret("2fb9e910b69240efaf8255ba3c2139d4")
//        .setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:8080/cli"))
//        .build()

//    val url = spotifyApi.authorizationCodeUri()
//        .scope(
//            listOf(
//                "app-remote-control",
//                "user-read-playback-position",
//                "user-read-playback-state",
//                "user-modify-playback-state",
//                "user-read-currently-playing",
//                "user-read-recently-played",
//                "user-top-read",
//                "user-read-private",
//                "user-read-email",
//                "ugc-image-upload"
//            ).joinToString(" ")
//        )
//        .build()
//
//    OAuthCallbackServer.startCallbackServer()
//
//    println("Authenticating, please navigate to: ${url.uri}")
//
//    while (OAuthCredentialsProvider.oAuthCallbackCode == null) {
//    }
//
//    println("STOPPING OAUTH CALLBACK")
//    OAuthCallbackServer.stopCallbackServer()
//
//
//    println("Requesting token")
//    val clientCredentialsRequest = spotifyApi.authorizationCode(OAuthCredentialsProvider.oAuthCallbackCode).build()
//
//    val clientCredentials = clientCredentialsRequest.execute()

//    println("Setting access token")
//    spotifyApi.accessToken = clientCredentials.accessToken
//    spotifyApi.accessToken = "BQDYwSqJZtgLlfCJm2bi_uaD0dAOkttWDuPnxZDhAqF4RhW69lm7Wa64PqMsNdzaycbkDsz2PTET45cfpP7YKLXGzmoIIpjP8JM3VY-N4kXHnX1rMJ1dLpuq-liFh8UxRaUiqHgV6_yG5CSIwwuUQekGRxxvgWnAZ5YMSwLATZrXE2NQtKQex6NfnCVgVLNsmBKVOy0n0xe1"

//    println(clientCredentials.accessToken)

//    println("Get current user playback")
//    val req = spotifyApi.informationAboutUsersCurrentPlayback.build()
//
//    val response = req.execute()
//
//    println(response)
//
//    println("Start user's playback")
//    spotifyApi.startResumeUsersPlayback().device_id("3491db6461abce7434d0a6f19f3393e956705774").build().execute()
//}


fun main(args: Array<String>) = Spot()
    .context { helpFormatter = ColorHelpFormatter }
    .completionOption(help = "Generate the completion script for the Spotify CLI. Usage = $COMMAND --generate-completion [bash zsh fish] > /completion/script/location/strm-completions.sh")
    .versionOption(CliContext.VERSION, names = setOf("-v", "--version"), message = { "Spotify CLI version: $it" })
    .main(args)

class Spot : CliktCommand(
    name = COMMAND,
    help = "Command Line Interface for Spotify",
    epilog = "Source code: https://github.com/trietsch/spotify-cli - Created by Robin Trietsch (https://trietsch.dev)"
) {
    companion object {
        internal const val COMMAND = "spot"
    }

    private val verbose by option("--verbose", hidden = true).flag(default = false)

    init {
        subcommands(
            Authentication()
        )
    }

    override fun run() {
        CliContext.VERBOSE_LOGGING = verbose
    }
}
