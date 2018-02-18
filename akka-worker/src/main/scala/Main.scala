import actor.WorkerActor
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.{ActorMaterializer, StreamTcpException}
import akka.util.Timeout
import service.{HealthCheckService, JsonSupport}

import scala.concurrent.duration._


object Main extends JsonSupport {

  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case _: StreamTcpException =>
        extractUri { uri =>
          println(s"Request to $uri could not be handled normally")
          complete(HttpResponse(StatusCodes.InternalServerError, entity = "Bad numbers, bad result!!!"))
        }
    }

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