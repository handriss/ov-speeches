package exception

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import akka.stream.StreamTcpException

trait CustomExceptionHandler {

  implicit def exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case _: StreamTcpException =>
        extractUri { uri =>
          complete("""{"success":false}""")
        }
    }
}
