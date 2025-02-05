package app.wishingtree.views

import java.nio.file.{Files, Path}
import scala.collection.mutable
import scala.util.*
import dev.wishingtree.branch.macaroni.fs.PathOps.*
import dev.wishingtree.branch.mustachio.Stache

trait TemplateLoader {
  def loadTemplate(path: Path): Try[String]

  def loadTemplateResponse(
      path: Path,
      stache: Stache,
      partials: Option[Stache]
  ): Try[TemplateResponse] =
    loadTemplate(path)
      .map(TemplateResponse(_, stache, partials))
}

object TemplateLoader {
  def apply(): TemplateLoader       =
    TemplateLoaderImpl()
  lazy val instance: TemplateLoader =
    TemplateLoader()
}

case class TemplateLoaderImpl() extends TemplateLoader {

  private val templateMap: mutable.Map[Path, String] = mutable.Map()

  extension (t: Try[String]) {
    private def addToMap(key: Path): Try[String] = {
      t.foreach { value =>
        templateMap.put(key, value)
      }
      t
    }
  }

  private def loadFromResources(path: Path): Try[String] = {
    Using(
      getClass.getClassLoader.getResourceAsStream(
        (>> / "templates" / path).toString
      )
    ) { stream =>
      new String(stream.readAllBytes())
    }
  }

  private def loadFromFileSystem(path: Path): Try[String] = {
    Using(
      Files.newInputStream(path)
    ) { stream =>
      new String(stream.readAllBytes())
    }
  }

  override def loadTemplate(path: Path): Try[String] = {

    templateMap.get(path) match {
      case Some(template) =>
        Success(template)
      case None           =>
        loadFromFileSystem(path)
          .orElse(loadFromResources(path))
          .addToMap(path)
    }

  }

}
