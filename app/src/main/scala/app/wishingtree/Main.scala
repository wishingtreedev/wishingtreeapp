package app.wishingtree

import app.wishingtree.routing.Routing.appEndpoint
import app.wishingtree.views.TemplateResponse
import app.wishingtree.views.TemplateResponse.templateBody
import dev.wishingtree.branch.macaroni.fs.PathOps.*
import dev.wishingtree.branch.mustachio.Stache
import dev.wishingtree.branch.mustachio.Stache.Str
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import sttp.tapir.*
import sttp.tapir.server.pekkohttp.PekkoHttpServerInterpreter

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.language.experimental.betterFors

object Main {

  val templateTest =
    appEndpoint.get
      .in("")
      .in(query[Option[String]]("name"))
      .out(templateBody)

  implicit val ec: scala.concurrent.ExecutionContext =
    scala.concurrent.ExecutionContext.global

  val templateRoute =
    templateTest.serverLogic[Future] { maybeName =>
      for {
        pretendToFetchData <- Future.successful {
                                maybeName.getOrElse("Wishing Tree")
                              }
        stache              = Stache.obj(
                                "name" -> Str(pretendToFetchData)
                              )
      } yield TemplateResponse(>> / "hello.mustache", stache, None)
    }

  def main(args: Array[String]): Unit = {

    given system: ActorSystem =
      ActorSystem()

    given executionContext: ExecutionContextExecutor =
      system.dispatcher

    val routes =
      PekkoHttpServerInterpreter()
        .toRoute(templateRoute)

    val bindingFuture =
      Http()
        .newServerAt("localhost", 9000)
        .bind(routes)

    println(s"Server online at http://localhost:9000/\nPress RETURN to stop...")
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }
}
