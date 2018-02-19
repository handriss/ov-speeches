import actor.WebScraperActor
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import client.ElasticClient
import exception.CustomExceptionHandler
import service.{HealthCheckService, ScraperService, Speech}

import scala.collection.immutable.IndexedSeq
import scala.concurrent.duration._


object Main extends CustomExceptionHandler {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("Main")
    implicit val materializer = ActorMaterializer()
    implicit val timeout = Timeout(5.seconds)
    implicit val executionContext = system.dispatcher

    val worker: ActorRef = system.actorOf(Props[WebScraperActor], "WebScraperActor")
    val healthCheckService = new HealthCheckService()
    val scraperService = new ScraperService()
    val client = new ElasticClient()

    val healthCheck: Route = (get & (path("healthCheck") | path("healthcheck"))) {
      complete(healthCheckService.execute())
    }
    val example: Route = (get & path("example")){
      val speeches: IndexedSeq[Speech] = scraperService.scrape()
      client.postSpeech(speeches.head)
      complete("""example called""")
    }

    val routes: Route = healthCheck ~ example

    Http().bindAndHandle(routes, "0.0.0.0", 8080)

  }
}

//set up config
//find out how api should look like, from frontend to akka, from akka to elastic