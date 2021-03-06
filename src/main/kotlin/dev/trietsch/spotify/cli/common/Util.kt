package dev.trietsch.spotify.cli.common

import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.output.HelpFormatter
import com.wrapper.spotify.exceptions.detailed.UnauthorizedException
import dev.trietsch.spotify.cli.Spot
import dev.trietsch.spotify.cli.commands.Authentication
import dev.trietsch.spotify.cli.commands.Login
import dev.trietsch.spotify.cli.common.CliContext.Terminal.SPOTIFY_GREEN
import dev.trietsch.spotify.cli.common.CliContext.Terminal.TERM_COLORS
import dev.trietsch.spotify.cli.common.CliContext.getCredentials

fun runIfAuthenticated(block: () -> Unit) = getCredentials()
    ?.let {
        runCatching(block)
            .onFailure {
                if (it is UnauthorizedException) {
                    println("Your credentials have expired. Please login to request an access token using: ${Spot.COMMAND} ${Authentication.COMMAND} ${Login.COMMAND}")
                } else {
                    println("An unexpected error occurred, enable verbose logging to see the stacktrace: ${Spot.COMMAND} --verbose <your command>")
                    printVerbose(it)
                }
            }
    }
    ?: println("You are currently not logged in. Please login to request an access token using: ${Spot.COMMAND} ${Authentication.COMMAND} ${Login.COMMAND}")

fun printVerbose(vararg messages: Any?) {
    if (CliContext.VERBOSE_LOGGING) {
        messages.forEach { println(it) }
    }
}

object BrowserUtil {
    private val runtime = Runtime.getRuntime()
    private val browsers = listOf(
        "epiphany", "firefox", "mozilla", "konqueror",
        "netscape", "opera", "links", "lynx", "chrome", "chromium"
    )

    fun openUrlInBrowser(url: String) {
        val os = System.getProperty("os.name").toLowerCase()

        when {
            os.indexOf("win") >= 0 -> runtime.exec("rundll32 url.dll,FileProtocolHandler $url");
            os.indexOf("mac") >= 0 -> runtime.exec("open $url")
            os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 -> {
                val command = StringBuilder().apply {
                    browsers.forEachIndexed { index, browser ->
                        when (index) {
                            0 -> append("""$browser "$url"""")
                            else -> append(""" || $browser "$url"""")
                        }

                    }
                }.toString()

                runtime.exec(arrayOf("sh", "-c", command))
            }
        }
    }
}

object ColorHelpFormatter : CliktHelpFormatter() {
    override fun renderTag(tag: String, value: String) = TERM_COLORS.green(super.renderTag(tag, value))
    override fun renderOptionName(name: String) = super.renderOptionName(name)
    override fun renderArgumentName(name: String) = SPOTIFY_GREEN(super.renderArgumentName(name))
    override fun renderSubcommandName(name: String) = SPOTIFY_GREEN(super.renderSubcommandName(name))

    override fun renderSectionTitle(title: String) =
        (TERM_COLORS.bold + TERM_COLORS.underline)(super.renderSectionTitle(title))

    override fun optionMetavar(option: HelpFormatter.ParameterHelp.Option) =
        TERM_COLORS.green(super.optionMetavar(option))
}
