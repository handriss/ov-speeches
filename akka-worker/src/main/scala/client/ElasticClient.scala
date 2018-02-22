package client

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import com.typesafe.config.{Config, ConfigFactory}
import service.Speech
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val speechFormat: RootJsonFormat[Speech] = jsonFormat4(Speech)
}

class ElasticClient(implicit val ec: ExecutionContext, implicit val ac: ActorSystem) extends JsonSupport{

  val config: Config = ConfigFactory.load()
  val SpeechType: String = "speech"
  val PutIfAbsent: String = "_create"
  val log = Logging(ac, "ElasticClient")

  def postSpeech(speech: Speech) = {
    Marshal(speech).to[MessageEntity].flatMap { entity =>
      val response: Future[HttpResponse] = Http().singleRequest(HttpRequest(
        uri = s"${config.getString("elasticsearch.index-url")}$SpeechType${speech.uid}$PutIfAbsent",
        method = HttpMethods.POST,
        entity = entity
      ))
      response.onComplete {
        case Success(HttpResponse(StatusCodes.Created, _, payload, _)) =>
          log.info("Successfully indexed {}", payload)
        case Success(HttpResponse(statusCode, _, _, _)) =>
          log.info("Document is already indexed, rejected with status code {}", statusCode)
        case Failure(reason) => log.info("Indexing failed, {}", reason)
      }
      response
    }
  }

  def postSpeech(speeches: Seq[Speech]): Unit = {
//    TODO: implement bulk api call
    speeches.foreach{ speech => postSpeech(speech)}
  }
}
