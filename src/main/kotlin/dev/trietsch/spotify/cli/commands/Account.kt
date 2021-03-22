package dev.trietsch.spotify.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.trietsch.spotify.cli.common.CliContext.SPOTIFY_API
import dev.trietsch.spotify.cli.common.runIfAuthenticated

class Account : CliktCommand(
    name = COMMAND,
    help = "Account information",
    printHelpOnEmptyArgs = true
) {
    companion object {
        internal const val COMMAND = "account"
    }

    init {
        subcommands(
            Whoami()
        )
    }

    override fun run() = Unit
}

class Whoami : CliktCommand(
    name = COMMAND,
    help = "Who am I?"
) {
    companion object {
        internal const val COMMAND = "whoami"
    }

    override fun run() {
        runIfAuthenticated {
            SPOTIFY_API.currentUsersProfile
                .build()
                .execute()
                .let {
                    println("You are ${it.displayName}!")
                }
        }
    }
}
