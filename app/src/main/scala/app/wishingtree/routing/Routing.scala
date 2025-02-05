package app.wishingtree.routing

import app.wishingtree.views.TemplateResponse
import app.wishingtree.views.TemplateResponse.*
import sttp.model.StatusCode
import sttp.tapir.*

object Routing {

  private case class HttpError(
      statusCode: StatusCode,
      templateResponse: TemplateResponse
  ) extends Throwable

  val defaultError: (StatusCode, TemplateResponse) =
    (StatusCode.InternalServerError, TemplateResponse.InternServerErrorTemplate)

  val errorMapping
      : PartialFunction[Throwable, (StatusCode, TemplateResponse)] = { case _ =>
    (StatusCode.NotFound, TemplateResponse.NotFoundTemplate)
  }

  val appEndpoint: Endpoint[Unit, Unit, Throwable, Unit, Any] =
    endpoint
      .errorOut(statusCode and templateBody)
      .mapErrorOut[Throwable](t => HttpError(t._1, t._2))(
        errorMapping.applyOrElse(_, _ => defaultError)
      )

}
