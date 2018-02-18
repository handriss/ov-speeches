package actor

import actor.WorkerActor._
import akka.actor.Actor
import akka.util.Timeout
import service.HealthCheckService
import akka.pattern.ask

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

object WorkerActor {
  case object HealthCheck
  case class HealthCheckResponse(akkaResponse: String, frontendResponse: String, elasticResponse: String)
}

class WorkerActor extends Actor {

  implicit val timeout = Timeout(5.seconds)
  implicit val system = context.system
  implicit val ec: ExecutionContext = context.system.dispatcher

  val healthCheckService = new HealthCheckService()

  override def receive: Receive = {
    case HealthCheck => healthCheckService.execute().flatMap(response => {
      sender ? Future.successful(response)
      Future.successful(response)
    })
  }
}
