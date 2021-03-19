package dev.trietsch.spotify.cli

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import dev.trietsch.spotify.cli.common.OAuthCallbackServer
import dev.trietsch.spotify.cli.common.OAuthCredentialsProvider


fun main(args: Array<String>) {
    val spotifyApi: SpotifyApi = SpotifyApi.Builder()
        .setClientId("91442357014c432589261382ca24c1ff")
        .setClientSecret("2fb9e910b69240efaf8255ba3c2139d4")
        .setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:8080/cli"))
        .build()

    val url = spotifyApi.authorizationCodeUri()
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

    OAuthCallbackServer.startCallbackServer()

//    Desktop.getDesktop().browse(url.uri)

    while (OAuthCredentialsProvider.oAuthCallbackCode == null) {
    }

    OAuthCallbackServer.stopCallbackServer()

    val clientCredentialsRequest = spotifyApi.authorizationCode(OAuthCredentialsProvider.oAuthCallbackCode).build()

    val clientCredentials = clientCredentialsRequest.execute()

    spotifyApi.accessToken = clientCredentials.accessToken

    println(clientCredentials.accessToken)

    val req = spotifyApi.informationAboutUsersCurrentPlayback.build()

    val response = req.execute()

    println(response.item)

    spotifyApi.startResumeUsersPlayback().build().execute()
}


//fun main(args: Array<String>) = Strm()
//    .context { helpFormatter = ColorHelpFormatter }
//    .completionOption(help = "Generate the completion script for the Stream Machine CLI. Usage = $COMMAND --generate-completion [bash zsh fish] > /completion/script/location/strm-completions.sh")
//    .versionOption(Common.VERSION, names = setOf("-v", "--version"), message = { "Stream Machine CLI version: $it" })
//    .main(args)

//class Strm : CliktCommand(
//    name = COMMAND,
//    help = "Command Line Interface for https://streammachine.io",
//    epilog = "Docs: https://docs.streammachine.io - Gitter: https://gitter.im/stream-machine"
//) {
//    companion object {
//        internal const val COMMAND = "strm"
//    }
//
//    private val verbose by option("--verbose", hidden = true).flag(default = false)
//
//    init {
//        subcommands(
//            Authentication(),
//            Streams(),
//            Outputs(),
//            Exporters(),
//            ConsentLevels(),
//            Sinks()
//        )
//    }
//
//    override fun run() {
//        Common.VERBOSE_LOGGING = verbose
//        printUpdateMessageIfAvailable()
//        initializeFuel()
//    }
//}
