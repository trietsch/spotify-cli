package dev.trietsch.spotify.cli

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.versionOption
import dev.trietsch.spotify.cli.Spot.Companion.COMMAND
import dev.trietsch.spotify.cli.commands.Account
import dev.trietsch.spotify.cli.commands.Authentication
import dev.trietsch.spotify.cli.commands.Playback
import dev.trietsch.spotify.cli.common.CliContext
import dev.trietsch.spotify.cli.common.ColorHelpFormatter

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
            Authentication(),
            Playback(),
            Account()
        )
    }

    override fun run() {
        CliContext.VERBOSE_LOGGING = verbose
    }
}
