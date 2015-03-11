package org.w3.example

/* We will write a simple application that can collect hash-URIs and
 * RDF literals of a given datatype.
 * 
 * We have never said this would be a useful application :-)
 */

import org.w3.banana._

/* You need to make your classes/methods parameterized by a given
 * `RDF` type. This is defined in [[org.w3.banana.RDF]] and this is
 * where the whole type hierarchy for the RDF model can be found. By
 * convention in banana-rdf, we use the type variable `Rdf`.
 * 
 * Actual operations are made available in typeclass
 * instances. [[org.w3.banana.RDFOps]] is the one you will almost
 * always need as it brings all the functions to manipulate the RDF
 * model. By convention in banana-rdf, you should always call it
 * `ops`.
 * 
 * For an introduction to this pattern, read
 * http://bertails.org/2015/02/15/abstract-algebraic-data-type.
 */
class Collector[Rdf <: RDF](implicit
  ops: RDFOps[Rdf]
) {

  // this makes all the main operations available to you, as well as
  // helpers
  import ops._

  def collectNodes(graph: Rdf#Graph, filterDatatype: Rdf#URI): Iterable[Rdf#Node] = {

    // implements the actual logic
    def keepNode(node: Rdf#Node): Option[Rdf#Node] =
      // NEVER use type introspection to know what kind of node you
      // are dealing with (it wouldn't work anyway because of type
      // erasure). Instead, you can `fold` the node and pass the 3
      // functions able to deal with each case. It is somewhere
      // between pattern matching and the visitor pattern. The partial
      // functions being used here use the extractors made available
      // in `RDFOps`.
      node.fold(
        // the Rdf#URI case
        {
          case uri @ URI(_) if uri.fragment.isDefined => Some(uri)
          case _                                      => None
        },
        // the Rdf#BNode case
        bnode => None,
        // the Rdf#Literal case
        {
          case lit @ Literal(lexicalForm, `filterDatatype`, None) => Some(lit)
          case _                                                  => None
        }
      )

    val nodes: Iterable[Rdf#Node] = graph.triples.flatMap { case Triple(s, p, o) =>
      keepNode(s).toIterable ++ keepNode(p).toIterable ++ keepNode(o).toIterable
    }

    nodes
  }

}
