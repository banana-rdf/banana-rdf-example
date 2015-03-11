package org.w3.example

import org.w3.banana._, io._, n3js._
import scala.concurrent._
import scalajs.concurrent.JSExecutionContext.Implicits.runNow

object N3jsImplicits {

  // an implementation of [[LDClient]] for N3js and XHR. This can only
  // work in an environment where XHR is available
  implicit val N3jsLDClient = new LDClient[N3js] {

    val turtleReader = implicitly[RDFReader[N3js, Future, Turtle]]

    def get(url: String): Future[N3js#Graph] = {
      import org.scalajs.dom.ext._
      println(">> "+url)
      Ajax.get(url).flatMap { xhr =>
        println(xhr.responseText)
        val input = new java.io.StringReader(xhr.responseText)
        turtleReader.read(input, url)
      }
    }

  }

  implicit val N3jsCollector = new Collector[N3js]

}
