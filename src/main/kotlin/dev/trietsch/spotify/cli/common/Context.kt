package dev.trietsch.spotify.cli.common

import com.github.ajalt.mordant.TermColors
import com.google.gson.GsonBuilder
import com.typesafe.config.ConfigFactory
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.StandardCharsets

/**
 * Context for the CLI to operate in. Purpose is to hold data that is easily retrieved throughout the whole CLI
 */
object CliContext {
    object Terminal {
        internal val TERM_COLORS = TermColors()

        internal val SPOTIFY_GREEN = TERM_COLORS.rgb("1DB954")
    }

    object OAuthCallbackProvider {
        var code: String? = null
    }

    internal val CONFIG = ConfigFactory.load()
        .let {
            CliConfig(
                it.getString("cli.clientId"),
                it.getString("cli.clientSecret"),
                CliConfig.CallbackConfig(
                    it.getInt("cli.callback.port"),
                    it.getString("cli.callback.path")
                )
            )
        }

    internal val GSON = GsonBuilder().create()

    private var CREDENTIALS: AuthorizationCodeCredentials? = null
    private var PREFERENCES: CliPreferences? = null

    internal var VERBOSE_LOGGING = false
    internal val VERSION = CliContext.javaClass.`package`.implementationVersion ?: "snapshot"

    internal val SPOTIFY_API: SpotifyApi = SpotifyApi.Builder().apply {
        setClientId(CONFIG.clientId)
        setClientSecret(CONFIG.clientSecret)
        setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:${CONFIG.callback.port}${CONFIG.callback.path}"))

        getCredentials()?.let { setAccessToken(it.accessToken) }
    }.build()

    internal fun getConfigPath() = File("${System.getProperty("user.home")}/.config/spotify-cli")
    internal fun getCredentialsFile() = getConfigPath().resolve("credentials.json")
    internal fun getPreferencesFile() = getConfigPath().resolve("preferences.json")

    internal fun credentialsWriter(block: (FileWriter) -> Unit) = writer(getCredentialsFile(), block)
    internal fun preferencesWriter(block: (FileWriter) -> Unit) = writer(getPreferencesFile(), block)
    private fun writer(file: File, block: (FileWriter) -> Unit) {
        getConfigPath().mkdirs()
        val fileWriter = FileWriter(file, StandardCharsets.UTF_8, false)
        block.invoke(fileWriter)
        fileWriter.flush()
        fileWriter.close()
    }

    internal fun getCredentials(): AuthorizationCodeCredentials? {
        if (CREDENTIALS == null) {
            CREDENTIALS =
                runCatching { GSON.fromJson(FileReader(getCredentialsFile()), AuthorizationCodeCredentials::class.java) }.getOrNull()
        }

        return CREDENTIALS
    }

    internal fun getPreferences(): CliPreferences? {
        if (PREFERENCES == null) {
            PREFERENCES = runCatching { GSON.fromJson(FileReader(getPreferencesFile()), CliPreferences::class.java) }.getOrNull()

            if (PREFERENCES == null) {
                PREFERENCES = CliPreferences()
            }
        }

        return PREFERENCES
    }
}

data class CliConfig(
    val clientId: String,
    val clientSecret: String,
    val callback: CallbackConfig
) {
    data class CallbackConfig(
        val port: Int,
        val path: String
    )
}

data class CliPreferences(
    val defaultPlaybackDeviceId: String? = null
)
