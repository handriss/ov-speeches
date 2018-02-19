package actor

import akka.actor.{Actor, Timers}
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object WebScraperActor {
  private case object TickKey
  private case object FirstTick
  private case object Tick
}

class WebScraperActor extends Actor with Timers {

  import actor.WebScraperActor._

  implicit val timeout = Timeout(5.seconds)
  implicit val system = context.system
  implicit val ec: ExecutionContext = context.system.dispatcher

  timers.startSingleTimer(TickKey, FirstTick, 1.second)

  override def receive: Receive = {
    case FirstTick =>
      println("firstTick received...")
      timers.startPeriodicTimer(TickKey, Tick, 30.seconds)
    case Tick =>
      println("tick received....")
  }
}
