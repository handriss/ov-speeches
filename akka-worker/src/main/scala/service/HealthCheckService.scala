package service

import actor.WorkerActor.HealthCheckResponse
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCode, StatusCodes}
import spray.json.DefaultJsonProtocol

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

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
