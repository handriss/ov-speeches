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
import spray.json._
import DefaultJsonProtocol._
import akka.util.ByteString

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val speechFormat: RootJsonFormat[Speech] = jsonFormat5(Speech)
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
    val entity = generateBulkRequestEntity(speeches)
    val response: Future[HttpResponse] = Http().singleRequest(HttpRequest(
      uri = s"${config.getString("elasticsearch.index-url")}/_bulk",
      method = HttpMethods.POST,
      entity = HttpEntity.Strict(MediaTypes.`application/json`, ByteString(entity))
    ))
    response.onComplete {
      case Success(HttpResponse(StatusCodes.Created, _, payload, _)) =>
        log.info("Bulk indexing successful.")
      case Success(HttpResponse(statusCode, x, y, z)) =>
        log.info("Document is already indexed, rejected with status code \n{} \n{} \n{} \n{}", statusCode, x, y, z)
      case Failure(reason) => log.info("Indexing failed, {}", reason)
    }

  }

  private def generateBulkRequestEntity(speeches: Seq[Speech]): String = {
    val speechAggregator: (String, Speech) => String = (aggregatedSpeeches, currentSpeech) => aggregatedSpeeches + "\n" + "{\"index\":{}}\n" + currentSpeech.toJson
    speeches.aggregate("")(speechAggregator, _ + _) + "\n"
  }
}
