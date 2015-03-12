package org.w3.example

import org.w3.banana._
import utest._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

abstract class StoreTest[Rdf <: RDF, Store](implicit
  ops: RDFOps[Rdf],
  ldclient: LDClient[Rdf],
  rdfStore: RDFStore[Rdf, Try, Store]
) extends TestSuite {

  import ops._
  import rdfStore.graphStoreSyntax._

  def makeStore(): Store

  import scala.language.implicitConversions
  implicit def convertTryToFuture[T](t: Try[T]): Future[T] = Future.fromTry(t)

  def tests = TestSuite {
    "put/get graphs" - {

      import Future.fromTry

      val timblCard = "http://www.w3.org/People/Berners-Lee/card"

      val store: Store = makeStore()

      for {
        graph <- ldclient.get(timblCard+".ttl")
        _ <- store.appendToGraph(URI(timblCard), graph)
        retrievedGraph <- store.getGraph(URI(timblCard))
      } yield {
        assert(retrievedGraph isIsomorphicWith graph)
      }

    }

  }

}
