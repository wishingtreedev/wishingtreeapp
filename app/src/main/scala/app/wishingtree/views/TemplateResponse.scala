package app.wishingtree.views

import dev.wishingtree.branch.macaroni.fs.PathOps.*
import dev.wishingtree.branch.mustachio.{Mustachio, Stache}
import sttp.tapir.*
import sttp.tapir.CodecFormat.TextHtml
import sttp.tapir.DecodeResult.Error

import java.nio.charset.Charset
import java.nio.file.Path

sealed trait RoutingError
case object NotFound extends RoutingError

case class TemplateResponse(
    template: String,
    context: Stache,
    partials: Option[Stache]
)

object TemplateResponse {

  def apply(
      path: Path,
      context: Stache,
      partials: Option[Stache]
  ): Either[Throwable, TemplateResponse] =
    TemplateLoader.instance
      .loadTemplateResponse(path, context, partials)
      .toEither

  lazy val NotFoundTemplate: TemplateResponse =
    TemplateLoader.instance
      .loadTemplateResponse(>> / "404.mustache", Stache.empty, None)
      .get // TODO

  lazy val InternServerErrorTemplate: TemplateResponse =
    TemplateLoader.instance
      .loadTemplateResponse(>> / "500.mustache", Stache.empty, None)
      .get // TODO

  private val encode: TemplateResponse => String = { view =>
    Mustachio.render(view.template, view.context, view.partials)
  }

  private def decode(s: String): DecodeResult[TemplateResponse] = {
    Error(s, new Exception("ViewResponse decoding not supported"))
  }

  private val htmlCodec: Codec[String, TemplateResponse, TextHtml] =
    Codec.string
      .mapDecode[TemplateResponse](decode)(encode)
      .format(TextHtml())

  val templateBody: EndpointOutput[TemplateResponse] = {
    EndpointIO.Body(
      RawBodyType.StringBody(Charset.defaultCharset()),
      htmlCodec,
      EndpointIO.Info.empty
    )
  }

}
