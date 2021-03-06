import actor.WebScraperActor
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import client.ElasticClient
import com.typesafe.config.ConfigFactory
import exception.CustomExceptionHandler
import service.{HealthCheckService, ScraperService}

import scala.concurrent.duration._


object Main extends CustomExceptionHandler {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("Main")
    implicit val materializer = ActorMaterializer()
    implicit val timeout = Timeout(5.seconds)
    implicit val executionContext = system.dispatcher
    val config = ConfigFactory.load()


    val healthCheckService = new HealthCheckService()
    val scraperService = new ScraperService()
    val client = new ElasticClient()
    val worker: ActorRef = system.actorOf(Props(classOf[WebScraperActor], scraperService, client), "WebScraperActor")

    val log = Logging(system, "Main")
    log.debug("Runner IS UP BABY")

    val healthCheck: Route = (get & (path("healthCheck") | path("healthcheck"))) {
      complete(healthCheckService.execute())
    }
    val example: Route = (get & path("example")){
      complete("""example called""")
    }

    val routes: Route = healthCheck ~ example

    Http().bindAndHandle(
      routes,
      interface = config.getString("http.host"),
      port = config.getInt("http.port")
    )

  }
}
