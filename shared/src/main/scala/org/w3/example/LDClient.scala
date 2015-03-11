package org.w3.example

import org.w3.banana.RDF
import scala.concurrent.Future

/** A Linked Data client that knows how to GET a Turtle document on
  * the Web.
  */
trait LDClient[Rdf <: RDF] {

  def get(url: String): Future[Rdf#Graph]

}
