package service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext

case class HealthCheckResponse(akkaResponse: String, frontendResponse: String, elasticResponse: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val healthCheckResponseFormat = jsonFormat3(HealthCheckResponse)
}

class HealthCheckService(implicit ec: ExecutionContext, implicit val system: ActorSystem) {

  def execute() = {
    for {
      frontendResponse <- Http().singleRequest(HttpRequest(uri = "http://localhost:4200/"))
      elasticResponse <- Http().singleRequest(HttpRequest(uri = "http://localhost:9200/"))
      akkaResponse = HttpResponse(StatusCodes.OK)
    } yield HealthCheckResponse(akkaResponse.status.value, frontendResponse.status.value, elasticResponse.status.value)
  }
}
