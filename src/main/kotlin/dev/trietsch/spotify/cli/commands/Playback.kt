package dev.trietsch.spotify.cli.commands

import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.google.gson.JsonArray
import com.wrapper.spotify.model_objects.specification.Episode
import com.wrapper.spotify.model_objects.specification.Track
import dev.trietsch.spotify.cli.Spot
import dev.trietsch.spotify.cli.common.CliContext
import dev.trietsch.spotify.cli.common.CliContext.SPOTIFY_API
import dev.trietsch.spotify.cli.common.CliContext.getPreferences
import dev.trietsch.spotify.cli.common.runIfAuthenticated

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
            CurrentlyPlaying(),
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

    private val songIds by option(
        "-si",
        "--song-id",
        help = "The songs you want to play, by Spotify URI (e.g. spotify:track:3SO7GM797LPOJSNSht7Q6C)"
    ).multiple()

    override fun run() {
        runIfAuthenticated {
            getPreferences()
                ?.defaultPlaybackDeviceId
                ?.also {
                    if (songIds.isEmpty()) {
                        SPOTIFY_API.startResumeUsersPlayback()
                            .device_id(it)
                            .build().execute()
                    } else {
                        SPOTIFY_API.startResumeUsersPlayback()
                            .device_id(it)
                            .uris(JsonArray().apply { songIds.forEach { id -> add(id) } })
                            .build().execute()
                    }
                }
                ?: println("No default playback device set. Set an active playback device using: ${Spot.COMMAND} ${Playback.COMMAND} ${Devices.COMMAND} ${SetDevices.COMMAND} <device-name>")
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
            getPreferences()
                ?.defaultPlaybackDeviceId
                ?.also {
                    SPOTIFY_API.pauseUsersPlayback()
                        .device_id(it)
                        .build().execute()
                }
                ?: println("No default playback device set. Set an active playback device using: ${Spot.COMMAND} ${Playback.COMMAND} ${Devices.COMMAND} ${SetDevices.COMMAND} <device-name>")
        }
    }
}

class CurrentlyPlaying : CliktCommand(
    name = COMMAND,
    help = "Show the currently playing song"
) {
    companion object {
        internal const val COMMAND = "show"
    }

    override fun run() {
        runIfAuthenticated {
            SPOTIFY_API.informationAboutUsersCurrentPlayback
                .build()
                .execute()
                ?.let { ctx ->
                    when (ctx.item) {
                        is Episode -> println("You're playing an episode of something :) Playback info not available, needs to be implemented.")
                        is Track -> println("'${ctx.item.name}' by [${(ctx.item as Track).artists.joinToString(", ") { it.name }}]")
                    }
                }
                ?: println("There is no song playing at the moment.")
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
    }

    private val name by argument(
        "name", help = "The name of the device you want to set as default playback device",
        completionCandidates = CompletionCandidates.Custom.fromStdout("${Spot.COMMAND} ${Playback.COMMAND} ${Devices.COMMAND} ${ListDevices.COMMAND}")
    )

    override fun run() {
        runIfAuthenticated {
            val matchedDevice = SPOTIFY_API
                .usersAvailableDevices
                .build()
                .execute()
                .firstOrNull { it.name == name }

            matchedDevice?.let { device ->
                CliContext.preferencesWriter {
                    CliContext.GSON.toJson(getPreferences()?.copy(defaultPlaybackDeviceId = device.id), it)
                }
                println("Succesfully set '$name' as the default playback device.")
            }
                ?: println("The device '$name' cannot be found in the known devices in your account. Please select another device. List available devices using: ${Spot.COMMAND} ${Playback.COMMAND} ${Devices.COMMAND} ${ListDevices.COMMAND}")
        }
    }
}
