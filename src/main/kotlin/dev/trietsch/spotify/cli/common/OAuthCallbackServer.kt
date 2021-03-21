package dev.trietsch.spotify.cli.common

import dev.trietsch.spotify.cli.common.CliContext.CONFIG
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.util.thread.QueuedThreadPool
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


object OAuthCallbackServer {
    private val server = Server(QueuedThreadPool(5, 1, 120))

    fun createAndStartCallbackServer(): Server {
        server.apply {
            connectors = arrayOf(ServerConnector(OAuthCallbackServer.server)
                .apply { port = CONFIG.callback.port })

            handler = ServletHandler()
                .apply {
                    addServletWithMapping(OAuthCallbackServlet::class.java, CONFIG.callback.path)
                }

        }

        return server.also { it.start() }
    }

    fun stopCallbackServer() {
        object : Thread() {
            override fun run() {
                try {
                    server.stop()
                    printVerbose("Stopped callback server")
                } catch (ex: Exception) {
                    printVerbose("Failed to stop callback server")
                    printVerbose("Error: ", ex)
                }
            }
        }.start()
    }
}

@WebServlet(name = "OAuth2CallbackServlet", urlPatterns = ["/cli/*"])
class OAuthCallbackServlet : HttpServlet() {
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        printVerbose("Received OAuthCallback code: ${request.getParameter("code")}")

        CliContext.OAuthCallbackProvider.code = request.getParameter("code")

        response.writer.apply {
            println("Successfully logged in! Return to your terminal to start using the Spotify CLI.")
        }

        OAuthCallbackServer.stopCallbackServer()
    }
}
