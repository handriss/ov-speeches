import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import service.ScraperService


object HttpServer {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val service: ScraperService = new ScraperService()

    val healthCheck: Route = (get & (path("healthCheck") | path("healthcheck")))(complete("""{"success":true}"""))
    val exampleScraper: Route = (get & path("example"))(complete(service.returnExample()))

    val routes: Route = healthCheck ~ exampleScraper

    Http().bindAndHandle(routes, "0.0.0.0", 8080)

    println(s"Server online at http://localhost:8080/\n")
  }
}