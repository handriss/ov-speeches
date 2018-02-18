import actor.WorkerActor
import actor.WorkerActor.HealthCheck
import akka.actor.Status.Failure
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import service.JsonSupport

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Main extends JsonSupport {


  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("Main")
    implicit val materializer = ActorMaterializer()
    implicit val timeout = Timeout(5.seconds)
    implicit val executionContext = system.dispatcher
    val worker: ActorRef = system.actorOf(Props[WorkerActor], "WorkerActor")

    val healthCheck: Route = (get & (path("healthCheck") | path("healthcheck"))) {
      onComplete(worker ? HealthCheck) {
        case Success(value) => complete(s"The result was $value")
        case value => complete(s"The result was $value")
      }
//      (worker ? HealthCheck).foreach(response => println(response))
    }
    val routes: Route = healthCheck

    Http().bindAndHandle(routes, "0.0.0.0", 8080)

  }
}