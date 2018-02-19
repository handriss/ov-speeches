package client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, MessageEntity}
import service.Speech
import spray.json.DefaultJsonProtocol

import scala.concurrent.{ExecutionContext, Future}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val speechFormat = jsonFormat4(Speech)
}

class ElasticClient(implicit val ec: ExecutionContext, implicit val ac: ActorSystem) extends JsonSupport{

  def postSpeech(speech: Speech): Future[HttpResponse] = {
    Marshal(speech).to[MessageEntity].flatMap { entity =>
      Http().singleRequest(HttpRequest(
        uri = "http://localhost:9200/speeches/speech/1",
        method = HttpMethods.POST,
        entity = entity
      ))
    }
  }
}
