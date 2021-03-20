package dev.trietsch.spotify.cli.common

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.util.thread.QueuedThreadPool
import java.lang.Exception
import java.net.URI
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

object OAuthCallbackServer {
    private val server = Server(QueuedThreadPool(5, 1, 120))

    fun waitForCallback(uri: URI) {
        server.connectors = arrayOf(ServerConnector(server)
            .apply { port = 8080 })
        server.handler = ServletHandler()
            .apply {
                addServletWithMapping(OAuthCallbackServlet::class.java, "/cli")
            }

        server.start()
        BrowserUtil.openUrlInBrowser(uri.toString())
        server.join()
    }

    fun stopCallbackServer() {
        object : Thread() {
            override fun run() {
                try {
                    server.stop()
                    println("Stopped server")
                } catch (ex: Exception) {
                    println("Failed to stop jetty")
                }
            }
        }.start()
    }
}

@WebServlet(name = "OAuth2CallbackServlet", urlPatterns = ["/cli/*"])
class OAuthCallbackServlet : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        println("Received OAuthCallback code: ${req.getParameter("code")}")

        CliContext.OAuthCredentialsProvider.oAuthCallbackCode = req.getParameter("code")

        println("STOPPING OAUTH CALLBACK")
        OAuthCallbackServer.stopCallbackServer()
    }
}


