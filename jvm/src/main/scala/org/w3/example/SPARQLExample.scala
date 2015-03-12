package org.w3.example

import org.w3.banana._, io._, binder._, jena._
import scala.concurrent._
import scala.util._
import java.net.URL
import org.joda.time.LocalDate

class SPARQLExample[Rdf <: RDF](implicit
  ops: RDFOps[Rdf],
  sparqlOps: SparqlOps[Rdf],
  sparqlHttp: SparqlEngine[Rdf, Try, URL],
  recordBinder: RecordBinder[Rdf]
) {

  import ops._
  import recordBinder._
  import sparqlOps._
  import sparqlHttp.sparqlEngineSyntax._

  // foaf is already available somewhere in RDFOps so we can invoke it
  val foaf = FOAFPrefix[Rdf]

  // and we can easily create a new Prefix
  val ont = Prefix("ont", "http://dbpedia.org/ontology/")

  // holds the binders for xsd:gYear
  object GregorianYearBinder {

    val xsd_gYear = xsd("gYear")

    implicit val YearToLiteral = new ToLiteral[Rdf, Int] {
      def toLiteral(year: Int): Rdf#Literal = Literal(year.toString, xsd_gYear)
    }

    implicit val YearFromLiteral = new FromLiteral[Rdf, Int] {
      def fromLiteral(literal: Rdf#Literal): Try[Int] = {
        val Literal(lexicalForm, datatype, _) = literal
        if (datatype == xsd_gYear) {
          try {
            Success(lexicalForm.replaceAll("[+-].+$", "").toInt)
          } catch {
            case _: IllegalArgumentException => Failure(FailedConversion(s"${literal} is an xsd.gYear but is not an acceptable date"))
          }
        } else {
          Failure(FailedConversion(s"${literal} is not an xsd:gYear"))
        }
      }
    }

  }

  // We will try to retrieve actors
  case class Actor(
    abstrakt: String,
    alias: Option[String],
    birthYear: Option[Int]
  )

  object Actor {

    val clazz = foaf.Person
    implicit val classUris = classUrisFor[Actor](clazz)

    val abstrakt = property[String](ont("abstract"))
    val alias = optional[String](ont("alias"))
    import GregorianYearBinder._
    val birthYear = optional[Int](ont("birthYear"))

    implicit val binder = pgb[Actor](abstrakt, alias, birthYear)(Actor.apply, Actor.unapply)

  }

  def main(args: Array[String]): Unit = {

    val endpoint = new URL("http://dbpedia.org/sparql")

    val query = parseConstruct("""
PREFIX ont: <http://dbpedia.org/ontology/>

CONSTRUCT {
 ?actor ?p ?o
} WHERE {
 <http://dbpedia.org/resource/Star_Trek:_The_Original_Series> ont:starring ?actor .
 ?actor ?p ?o .
}
""").get

    val resultGraph = endpoint.executeConstruct(query).get

    // this demonstrates how to filter the triples in the graph. Here,
    // we remove the language tag from all the literal in the object
    // position. Note that we could have done in the SPARQL query
    // instead of programmatically.
    val graph = Graph {
      resultGraph.triples.collect { case t @ Triple(s, p, o) =>
        o.fold(
          uri => t,
          bnode => t,
          {
            case Literal(lexicalForm, _, Some(_)) => Triple(s, p, Literal(lexicalForm))
            case _                                => t
          }
        )
      }
    }

    // to get the actors, we first the Person-s, which will become
    // pointers of interest in the graph, and we use the binder to get
    // back to the case clas
    val actors = graph.triples.collect { case Triple(actor, rdf.typ, foaf.Person) =>
      val pg = PointedGraph(actor, graph)
      pg.as[Actor].toOption
    }.flatten

    // we finally print those actors
    actors.foreach(println)

  }

}

import org.w3.banana.jena._
import JenaImplicits._

object JenaSPARQLExample extends SPARQLExample[Jena]
