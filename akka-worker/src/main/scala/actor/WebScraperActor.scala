package actor

import akka.actor.{Actor, Timers}
import akka.event.Logging
import akka.util.Timeout
import client.ElasticClient
import service.{ScraperService, Speech}

import scala.collection.immutable.IndexedSeq
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object WebScraperActor {
  private case object TickKey
  private case object RunCrawler
  private case object Tick
}

class WebScraperActor(service: ScraperService, client: ElasticClient) extends Actor with Timers {

  import actor.WebScraperActor._

  implicit val timeout = Timeout(5.seconds)
  implicit val system = context.system
  implicit val ec: ExecutionContext = context.system.dispatcher
  val log = Logging(system, this)

  def runCrawler() = {
    val speeches: IndexedSeq[Speech] = service.scrape()
    client.postSpeech(speeches)
  }
  runCrawler()
  timers.startPeriodicTimer(TickKey, RunCrawler, 12.hours)

  override def receive: Receive = {
    case RunCrawler => runCrawler()
  }
}
