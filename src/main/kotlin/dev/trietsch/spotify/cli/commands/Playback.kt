package dev.trietsch.spotify.cli.commands

import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import dev.trietsch.spotify.cli.common.CliContext.SPOTIFY_API
import dev.trietsch.spotify.cli.common.runIfAuthenticated

// List current playback

// Start playback
// Start playback with song
// SPOTIFY_API.startResumeUsersPlayback().device_id("3491db6461abce7434d0a6f19f3393e956705774").build().execute()

// Stop playback

class Playback : CliktCommand(
    name = COMMAND,
    help = "Control playback",
    printHelpOnEmptyArgs = true
) {
    companion object {
        internal const val COMMAND = "playback"
    }

    init {
        subcommands(
            Play(),
            Pause(),
            Devices()
        )
    }

    override fun run() = Unit
}

class Play : CliktCommand(
    name = COMMAND,
    help = "Continue playing the current song"
) {
    companion object {
        internal const val COMMAND = "play"
    }

    override fun run() {
        runIfAuthenticated {
            SPOTIFY_API.startResumeUsersPlayback()
                .device_id("3491db6461abce7434d0a6f19f3393e956705774")
                .build().execute()
        }
    }
}

class Pause : CliktCommand(
    name = COMMAND,
    help = "Pause playback"
) {
    companion object {
        internal const val COMMAND = "pause"
    }

    override fun run() {
        runIfAuthenticated {
            SPOTIFY_API.pauseUsersPlayback()
                .device_id("3491db6461abce7434d0a6f19f3393e956705774")
                .build().execute()
        }
    }
}

class Devices : CliktCommand(
    name = COMMAND,
    help = "List and set playback device"
) {
    companion object {
        internal const val COMMAND = "devices"
    }

    init {
        subcommands(
            ListDevices(),
            SetDevices()
        )
    }

    override fun run() = Unit
}

class ListDevices : CliktCommand(
    name = COMMAND,
    help = "List playback devices"
) {
    companion object {
        internal const val COMMAND = "list"
    }

    override fun run() {
        runIfAuthenticated {
            println("listing devices")
            SPOTIFY_API
                .usersAvailableDevices
                .build()
                .execute()
                .joinToString(" ") { it.name }
                .let { println(it) }
        }
    }
}

class SetDevices : CliktCommand(
    name = COMMAND,
    help = "Set default playback device",
) {
    companion object {
        internal const val COMMAND = "set-default"

//        private val devices = runIfAuthenticated(false) {
//            SPOTIFY_API
//                .usersAvailableDevices
//                .build()
//                .execute()
//        }
    }

    private val name by argument("name", help = "The name of the device you want to set as default playback device",
    completionCandidates = CompletionCandidates.Custom.fromStdout("spot playback devices list"))

    override fun run() {
        // TODO actually store this
        println("Set default device: $name")
    }
}
