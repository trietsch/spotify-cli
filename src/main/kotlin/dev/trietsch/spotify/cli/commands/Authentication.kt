package dev.trietsch.spotify.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import dev.trietsch.spotify.cli.common.BrowserUtil
import dev.trietsch.spotify.cli.common.CliContext
import dev.trietsch.spotify.cli.common.CliContext.GSON
import dev.trietsch.spotify.cli.common.CliContext.SPOTIFY_API
import dev.trietsch.spotify.cli.common.CliContext.credentialsWriter
import dev.trietsch.spotify.cli.common.CliContext.getCredentialsFile
import dev.trietsch.spotify.cli.common.OAuthCallbackServer
import dev.trietsch.spotify.cli.common.Scopes.SCOPES
import dev.trietsch.spotify.cli.common.printVerbose

class Authentication : CliktCommand(
    name = COMMAND,
    help = "Authenticate with Spotify.",
    printHelpOnEmptyArgs = true
) {
    companion object {
        internal const val COMMAND = "auth"
    }

    init {
        subcommands(
            Login(),
            Revoke()
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
        val jettyServer = OAuthCallbackServer.createAndStartCallbackServer()

        val url = SPOTIFY_API.authorizationCodeUri()
            .scope(SCOPES)
            .build()

        println("Authenticating, please navigate to: ${url.uri}")
        BrowserUtil.openUrlInBrowser(url.uri.toString())

        jettyServer.join()

        printVerbose("Requesting token")
        val clientCredentials = SPOTIFY_API
            .authorizationCode(CliContext.OAuthCallbackProvider.code)
            .build()
            .execute()

        printVerbose("Received access token:", clientCredentials.accessToken, "")
        printVerbose("Storing access token")
        storeCredentials(clientCredentials)
    }

    private fun storeCredentials(clientCredentials: AuthorizationCodeCredentials) =
        runCatching {
            credentialsWriter { GSON.toJson(clientCredentials, it) }
        }.fold({
            println("Successfully logged in!")
        }) {
            println("Failed to store credentials at ${getCredentialsFile()}.")
            printVerbose("Error: ", it)
        }
}

class Revoke : CliktCommand(
    name = COMMAND,
    help = "Revoke the currently stored credentials"
) {
    companion object {
        internal const val COMMAND = "revoke"
    }

    override fun run() =
        runCatching {
            if (getCredentialsFile().exists()) getCredentialsFile().delete() else false
        }.fold({
            if (it) {
                println("Revoked currently active credentials.")
            } else {
                println("Currently there are no active credentials.")
            }
        }) {
            println("Failed to revoke currently active credentials.")
            println("reason = ${it.message}")
        }
}
