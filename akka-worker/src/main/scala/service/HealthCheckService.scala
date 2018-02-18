package service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}

import scala.concurrent.ExecutionContext

class HealthCheckService(implicit ec: ExecutionContext, implicit val system: ActorSystem) {

  def execute() = {
    for {
      frontendResponse <- Http().singleRequest(HttpRequest(uri = "http://localhost:4200/"))
      elasticResponse <- Http().singleRequest(HttpRequest(uri = "http://localhost:9200/"))
      akkaResponse = HttpResponse(StatusCodes.OK)
    } yield """{"success":true}"""
  }
}
