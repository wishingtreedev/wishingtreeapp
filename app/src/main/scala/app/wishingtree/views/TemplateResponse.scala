package app.wishingtree.views

import dev.wishingtree.branch.mustachio.{Mustachio, Stache}
import sttp.tapir.CodecFormat.TextHtml
import sttp.tapir.DecodeResult.Error
import sttp.tapir.*

import java.nio.charset.Charset

case class TemplateResponse(
    template: String,
    context: Stache,
    partials: Option[Stache]
)

object TemplateResponse {

  private val encode: TemplateResponse => String = { view =>
    val rendered = Mustachio.render(view.template, view.context, view.partials)
    rendered
  }

  private def decode(s: String): DecodeResult[TemplateResponse] = {
    Error(s, new Exception("ViewResponse decoding not supported"))
  }

  private val htmlCodec: Codec[String, TemplateResponse, TextHtml] =
    Codec.string
      .mapDecode[TemplateResponse](decode)(encode)
      .format(TextHtml())

  val templateBody: EndpointOutput[TemplateResponse] =
    EndpointIO.Body(
      RawBodyType.StringBody(Charset.defaultCharset()),
      htmlCodec,
      EndpointIO.Info.empty
    )

}
