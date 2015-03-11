package org.w3.example

import org.w3.banana._, io._, jena._
import scala.concurrent._
import scala.util._

object JenaImplicits {

  // an implementation of [[LDClient]] for Jena, which relies on HTTP Dispatch
  implicit val JenaLDClient = new LDClient[Jena] {

    val turtleReader = implicitly[RDFReader[Jena, Try, Turtle]]

    def get(urlS: String): Future[Jena#Graph] = {
      import dispatch._, Defaults._
      Http(url(urlS) OK as.String).flatMap { turtleDoc =>
        val sr = new java.io.StringReader(turtleDoc)
        turtleReader.read(sr, urlS) match {
          case Success(graph) => Future.successful(graph)
          case Failure(t)     => Future.failed(t)
        }
      }
    }

  }

  implicit val JenaCollector = new Collector[Jena]

}
