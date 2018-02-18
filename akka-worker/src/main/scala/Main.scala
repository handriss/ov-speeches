import actor.WorkerActor
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import exception.CustomExceptionHandler
import service.HealthCheckService

import scala.concurrent.duration._


object Main extends CustomExceptionHandler {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("Main")
    implicit val materializer = ActorMaterializer()
    implicit val timeout = Timeout(5.seconds)
    implicit val executionContext = system.dispatcher
    val worker: ActorRef = system.actorOf(Props[WorkerActor], "WorkerActor")
    val healthCheckService = new HealthCheckService()

    val healthCheck: Route = (get & (path("healthCheck") | path("healthcheck"))) {
      complete(healthCheckService.execute())
    }
    val routes: Route = healthCheck

    Http().bindAndHandle(routes, "0.0.0.0", 8080)

  }
}