package actor

import actor.WorkerActor._
import akka.actor.Actor
import akka.util.Timeout
import service.HealthCheckService
import akka.pattern.pipe

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

object WorkerActor {
  case object HealthCheck
}

class WorkerActor extends Actor {

  implicit val timeout = Timeout(5.seconds)
  implicit val system = context.system
  implicit val ec: ExecutionContext = context.system.dispatcher

  override def receive: Receive = {
    case HealthCheck => ""
  }
}
