package com.utility.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, ResponseEntity, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.{Flow, Framing, Keep, Sink, Source, SourceQueueWithComplete}
import akka.stream.{Materializer, OverflowStrategy, QueueOfferResult}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import spray.json.JsonReader

import java.net.URL
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}
import spray.json._

//private[client] : TODO add private[client]
trait RestAPIClient extends LazyLogging {
  protected val servicePrefix: String
  protected val baseUrl: String
  protected implicit val ec: ExecutionContext
  protected implicit val actorSystem: ActorSystem
  protected implicit val mat: Materializer
  protected val queueSize: Int
  protected val overflowStrategy: OverflowStrategy = OverflowStrategy.dropNew

  private lazy val connectionPool: Flow[(HttpRequest, Promise[HttpResponse]), (Try[HttpResponse], Promise[HttpResponse]), Http.HostConnectionPool] = {
    val url = new URL(baseUrl)
    val host = url.getHost
    val port = url.getPort
    if (url.getProtocol.toLowerCase == "https")
      Http().cachedHostConnectionPoolHttps[Promise[HttpResponse]](host, 443)
    else Http().cachedHostConnectionPool[Promise[HttpResponse]](host, port)

  }

  private lazy val queue: SourceQueueWithComplete[(HttpRequest, Promise[HttpResponse])] =
    Source.queue[(HttpRequest, Promise[HttpResponse])](queueSize, overflowStrategy)
      .via(connectionPool)
      .toMat(Sink.foreach({
        case (Success(resp), p) => p.success(resp)
        case (Failure(e), p) => p.failure(e)
      }))(Keep.left)
      .run()

  protected def queueRequest(request: HttpRequest): Future[HttpResponse] = {
    val responsePromise = Promise[HttpResponse]()
    queue.offer(request -> responsePromise).flatMap {
      case QueueOfferResult.Enqueued => responsePromise.future
      case QueueOfferResult.Dropped =>
        logger.error(s"Queue overflowed. Try again later.")
        Future.failed(new RuntimeException("Queue overflowed. Try again later."))
      case QueueOfferResult.Failure(ex) =>
        logger.error("QueueOfferResult.Failure(ex)", ex)
        Future.failed(ex)
      case QueueOfferResult.QueueClosed =>
        logger.error("Queue was closed (pool shut down) while running the request. Try again later.")
        Future.failed(new RuntimeException("Queue was closed (pool shut down) while running the request. Try again later."))
    }

    //    request.withUri(baseUrl+request.uri.toString())

    //        Http().singleRequest(request.withUri(baseUrl + request.uri.toString()))
  }

  protected def unmarshal[T: JsonReader](responseEntity: ResponseEntity): Future[T] = {
    Unmarshal(responseEntity).to[String].map {
      jsonStr =>
        Try {
          jsonStr.parseJson.convertTo[T]
        } match {
          case Success(value) => value
          case Failure(exception) =>
            logger.error(s"Exception occurred while parsing:${jsonStr}")
            throw exception
        }
    }
  }

  protected def get[Response: RootJsonReader](
                                               uri: String,
                                               queryParams: Map[String, String] = Map.empty,
                                               headers: Seq[(String, String)] = Seq.empty
                                             ): Future[Response] = {
    val rawHeaders = headers.map { case (key, value) => RawHeader(key, value) }.toList
    val httpReq = HttpRequest(method = HttpMethods.GET, headers = rawHeaders)
      .withUri(uri = Uri(uri).withQuery(Query(queryParams)))
    val startTime = System.currentTimeMillis()
    queueRequest(httpReq).flatMap {
      case HttpResponse(code, _, entity, _) =>
        if (code.isSuccess()) {
          logger.debug(s"$servicePrefix api call[GET]:$uri, completed successfully with status: ${code.intValue()} in in ${System.currentTimeMillis() - startTime} ms.")
          unmarshal[Response](entity).recoverWith {
            case ex: Exception =>
              logger.error(s"$servicePrefix api call [GET]:$uri, invalid Json received. Error: $ex.")
              Future.failed(new RuntimeException(s"$servicePrefix Unmarshall error. Cause:$ex."))

          }
        } else {
          Unmarshal(entity).to[String].flatMap {
            s =>
              val errorMsg = s"$servicePrefix api call [GET]:$uri, failed with the status: ${code.intValue()} in ${System.currentTimeMillis() - startTime} ms and response body: $s"
              logger.error(errorMsg)
              Future.failed(new RuntimeException(s"$servicePrefix api call [GET]:$uri, failed with status: ${code.intValue()} and response body: $s."))
          }
        }
    }.recoverWith {
      case serviceEx: RuntimeException => Future.failed(serviceEx)
      case exception: Exception =>
        Future.failed(new RuntimeException(s"$servicePrefix error in executing [GET]:$uri. Exception:${exception.getMessage}"))
    }
  }
  //  private implicit def toRootJsonFormat[T](implicit jsonReaderFormat: JsonReader[T]): RootJsonReader[T] = {
  //    val reader = implicitly[JsonReader[T]](jsonReaderFormat)
  //    (json: JsValue) => reader.read(json)
  //  }

  protected def post11[Request: JsonWriter](
                                             uri: String,
                                             body: Request,
                                             queryParams: Map[String, String] = Map.empty,
                                             headers: Seq[(String, String)] = Seq.empty
                                           ): Future[Array[String]] = {
    val entity = HttpEntity(ContentTypes.`application/json`, ByteString(body.toJson.compactPrint))
    val rawHeaders = headers.map { case (key, value) => RawHeader(key, value) }.toList
    val httpReq = HttpRequest(method = HttpMethods.POST, headers = rawHeaders, entity = entity)
      .withUri(uri = Uri(uri).withQuery(Query(queryParams)))
    val startTime = System.currentTimeMillis()
    queueRequest(httpReq).flatMap {
      response =>
        val dataBytes = response.entity.dataBytes
        val byteStringFuture = dataBytes.runFold(ByteString.empty)(_ ++ _)

        byteStringFuture.map {
          byteString =>
            val responseString = byteString.utf8String
            // Split the response string by closing brace, then re-add the brace
            val jsonObjects = responseString.split("},\\s*\\{").map {
              case str if str.startsWith("{") && str.endsWith("}") => str
              case str if str.startsWith("{") => str + "}"
              case str if str.endsWith("}") => "{" + str
              case str => "{" + str + "}"
            }
            jsonObjects
        }
    }
  }

  protected def post1[Request: JsonWriter](
                                            uri: String,
                                            body: Request,
                                            queryParams: Map[String, String] = Map.empty,
                                            headers: Seq[(String, String)] = Seq.empty
                                          ): Future[String] = {
    val entity = HttpEntity(ContentTypes.`application/json`, ByteString(body.toJson.compactPrint))
    val rawHeaders = headers.map { case (key, value) => RawHeader(key, value) }.toList
    val httpReq = HttpRequest(method = HttpMethods.POST, headers = rawHeaders, entity = entity)
      .withUri(uri = Uri(uri).withQuery(Query(queryParams)))
    val startTime = System.currentTimeMillis()
    queueRequest(httpReq).flatMap {
      case HttpResponse(code, headers, entity, _) =>
        if (code.isSuccess()) {
          logger.debug(s"$servicePrefix api call[POST]:$uri, completed successfully with status: ${code.intValue()} in ${System.currentTimeMillis() - startTime} ms.")
          Unmarshal(entity).to[String].recoverWith {
            case ex: Exception =>
              logger.error(s"$servicePrefix api call[POST]:$uri, invalid Json received. Error: $ex ")
              Future.failed(new RuntimeException(s"$servicePrefix Unmarshall error. Cause:$ex"))
          }
        } else {
          Unmarshal(entity).to[String].flatMap {
            s =>
              logger.error(
                s"$servicePrefix api call [POST]:$uri, failed with status: ${code.intValue()} and " +
                  s"response body: $s for the request payload: ${body.toJson}"
              )
              Future.failed(new RuntimeException(s"$servicePrefix api call [POST]:$uri, failed with status: ${code.intValue()} in in ${System.currentTimeMillis() - startTime} ms and response body: $s."))
          }
        }
    }.recoverWith {
      case serviceEx: RuntimeException => Future.failed(serviceEx)
      case exception: Exception =>
        logger.error(s"$servicePrefix error in executing [POST]:$uri. Exception:${exception.getMessage}")
        Future.failed(new RuntimeException(s"$servicePrefix error in executing [POST]:$uri. Exception:${exception.getMessage}"))
    }
  }

  protected def post[Request: JsonWriter, Response: JsonReader](
                                                                 uri: String,
                                                                 body: Request,
                                                                 queryParams: Map[String, String] = Map.empty,
                                                                 headers: Seq[(String, String)] = Seq.empty
                                                               ): Future[Response] = {
    val entity = HttpEntity(ContentTypes.`application/json`, ByteString(body.toJson.compactPrint))
    val rawHeaders = headers.map { case (key, value) => RawHeader(key, value) }.toList
    val httpReq = HttpRequest(method = HttpMethods.POST, headers = rawHeaders, entity = entity)
      .withUri(uri = Uri(uri).withQuery(Query(queryParams)))
    val startTime = System.currentTimeMillis()
    queueRequest(httpReq).flatMap {
      case HttpResponse(code, headers, entity, _) =>
        if (code.isSuccess()) {
          logger.debug(s"$servicePrefix api call[POST]:$uri, completed successfully with status: ${code.intValue()} in ${System.currentTimeMillis() - startTime} ms.")
          unmarshal[Response](entity).recoverWith {
            case ex: Exception =>
              logger.error(s"$servicePrefix api call[POST]:$uri, invalid Json received. Error: $ex ")
              Future.failed(new RuntimeException(s"$servicePrefix Unmarshall error. Cause:$ex"))
          }
        } else {
          Unmarshal(entity).to[String].flatMap {
            s =>
              logger.error(
                s"$servicePrefix api call [POST]:$uri, failed with status: ${code.intValue()} and " +
                  s"response body: $s for the request payload: ${body.toJson}"
              )
              Future.failed(new RuntimeException(s"$servicePrefix api call [POST]:$uri, failed with status: ${code.intValue()} in in ${System.currentTimeMillis() - startTime} ms and response body: $s."))
          }
        }
    }.recoverWith {
      case serviceEx: RuntimeException => Future.failed(serviceEx)
      case exception: Exception =>
        logger.error(s"$servicePrefix error in executing [POST]:$uri. Exception:${exception.getMessage}")
        Future.failed(new RuntimeException(s"$servicePrefix error in executing [POST]:$uri. Exception:${exception.getMessage}"))
    }
  }


  protected def put[Response: RootJsonReader, Request: JsonWriter](
                                                                    uri: String,
                                                                    body: Request,
                                                                    queryParams: Map[String, String] = Map.empty,
                                                                    headers: Seq[(String, String)] = Seq.empty
                                                                  ): Future[Response] = {
    val entity = HttpEntity(ContentTypes.`application/json`, ByteString(body.toJson.compactPrint))
    val rawHeaders = headers.map { case (key, value) => RawHeader(key, value) }.toList
    val httpReq = HttpRequest(method = HttpMethods.PUT, headers = rawHeaders, entity = entity)
      .withUri(uri = Uri(uri).withQuery(Query(queryParams)))
    val startTime = System.currentTimeMillis()
    queueRequest(httpReq).flatMap {
      case HttpResponse(code, _, entity, _) =>
        if (code.isSuccess()) {
          logger.debug(s"$servicePrefix api call[PUT]:$uri, completed successfully with status: ${code.intValue()} in ${System.currentTimeMillis() - startTime} ms")
          unmarshal[Response](entity).recoverWith {
            case ex: Exception =>
              logger.error(s"$servicePrefix api call[PUT]:$uri, invalid Json received. Error: $ex ")
              Future.failed(new RuntimeException(s"$servicePrefix Unmarshall error. Cause:$ex"))

          }
        } else {
          Unmarshal(entity).to[String].flatMap {
            s =>
              logger.error(
                s"$servicePrefix api call [PUT]:$uri, failed with status: ${code.intValue()} in ${System.currentTimeMillis() - startTime} ms and " +
                  s"response body: $s for the request payload: ${body.toJson}"
              )
              Future.failed(new RuntimeException(s"$servicePrefix api call [PUT]:$uri, failed with status: ${code.intValue()} and response body: $s."))
          }
        }
    }.recoverWith {
      case serviceEx: RuntimeException => Future.failed(serviceEx)
      case exception: Exception =>
        Future.failed(new RuntimeException(s"$servicePrefix error in executing [PUT]:$uri. Exception:${exception.getMessage}"))
    }
  }
}
