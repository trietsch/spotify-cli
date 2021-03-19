package dev.trietsch.spotify.cli.common

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.util.thread.QueuedThreadPool
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

object OAuthCredentialsProvider {
    // Yes, this is not the most beautiful, but it does the job :)
    var oAuthCallbackCode: String? = null
}

object OAuthCallbackServer {
    private val server = Server(QueuedThreadPool(5, 1, 120))

    fun startCallbackServer() {
        val connector = ServerConnector(server)
        connector.port = 8080
        server.connectors = arrayOf(connector)

        val servletHandler = ServletHandler()
        server.handler = servletHandler

        servletHandler.addServletWithMapping(OAuthCallbackServlet::class.java, "/cli")

        server.start()
    }

    fun stopCallbackServer() {
        server.stop()
    }
}

@WebServlet(name = "OAuth2CallbackServlet", urlPatterns = ["/cli/*"])
class OAuthCallbackServlet : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        println("Received OAuthCallback code: ${req.getParameter("code")}")

        OAuthCredentialsProvider.oAuthCallbackCode = req.getParameter("code")
    }
}


