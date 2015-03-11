package org.w3.example

import org.w3.banana._, io._
import utest._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

// the actual test depends on a [[LDClient]] and a [[Collector]]
class CollectorTest[Rdf <: RDF](implicit
  ops: RDFOps[Rdf],
  ldclient: LDClient[Rdf],
  collector: Collector[Rdf]
) extends TestSuite {

  import ops._

  def tests = TestSuite {
    "a simple example" - {
      for {
        graph <- ldclient.get("http://www.w3.org/People/Berners-Lee/card.ttl")
      } yield {
        val nodes = collector.collectNodes(graph, filterDatatype = xsd.hexBinary)
        val expectedNode = Literal("e7414af709522144520617d3531e6099c69932dfd5ec7cc534583d72b71a7d6b202aaa686c74fcc7a5beecbd06a98b24081b36229f2126e448542952c40e02093b30c86035e2bee3d6d0aab38266a1bea863d151c38f53247b8beef61afddf4c57c59841639e691e60145308933d967c59e3c4f23635b2171a46b22e8a4714a44f09d4f825cb0b9b645aaf4c2695ea86f67d3ee721130730375e223b0fc0a36733eb792962e6ebbe79c76cd9e779bb0401eea9a1d4ce3769cb2f1a43331ed4275fef8df64dddd7cfd2ba559b92fcd149b9cb27bb40659ff4b5ce3abc7b30d7980cdc3f8ad8f312d164547509b182298547964a07aa7e99a68e2f797a5631b0f5", xsd.hexBinary)
        assert(nodes.exists(_ == expectedNode))
      }
    }
  }

}
