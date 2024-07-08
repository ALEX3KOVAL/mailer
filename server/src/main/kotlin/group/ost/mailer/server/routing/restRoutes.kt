package group.ost.mailer.server.routing

import group.ost.mailer.server.routing.route.sendEmail
import io.ktor.server.routing.*

fun Route.restRoutes() {
    sendEmail()
}