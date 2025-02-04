package app.wishingtree

import app.wishingtree.views.TemplateResponse
import app.wishingtree.views.TemplateResponse.templateBody
import dev.wishingtree.branch.mustachio.Stache
import dev.wishingtree.branch.mustachio.Stache.Str
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import sttp.tapir.*
import sttp.tapir.server.pekkohttp.PekkoHttpServerInterpreter

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn

object Main {

  val template =
    """
      |<!DOCTYPE html>
      |<html>
      |<head>
      |    <title>Wishing Tree</title>
      |</head>
      |<body>
      |    <h1>Wishing Tree</h1>
      |    <p>Hello, {{name}}!</p>
      |</body>
      |</html>
      |""".stripMargin

  val templateTest =
    endpoint.get
      .in("")
      .in(query[Option[String]]("name"))
      .out(templateBody)

  val templateRoute =
    templateTest.serverLogicPure[Future](maybeName =>
      val stache =
        Stache.obj("name" -> Str(maybeName.getOrElse("Wishing Tree")))
      Right(TemplateResponse(template, stache, None))
    )

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
