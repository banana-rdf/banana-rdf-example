package org.w3.example

import org.w3.banana._, io._, n3js._
import scala.concurrent.Future

object Mocked {

  val cardTtl = """
     @prefix : <http://xmlns.com/foaf/0.1/> .
    @prefix ca: <#> .
    @prefix card: <http://www.w3.org/People/Berners-Lee/card#> .
    @prefix cc: <http://creativecommons.org/ns#> .
    @prefix cert: <http://www.w3.org/ns/auth/cert#> .
    @prefix dc: <http://purl.org/dc/elements/1.1/> .
    @prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> .
    @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
    @prefix web: <http://my-profile.eu/ns/webapp#> .
    @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
    
    <../../DesignIssues/Overview.html>     dc:title "Design Issues for the World Wide Web";
         :maker card:i .
    
    <>     rdf:type :PersonalProfileDocument;
         cc:license <http://creativecommons.org/licenses/by-nc/3.0/>;
         dc:title "Tim Berners-Lee's FOAF file";
         :maker card:i;
         :primaryTopic card:i .
    
    ca:findMyLoc     web:description "Share your location with your friends.";
         web:endpoint <https://timbl.data.fm/test2/locator/location>;
         web:name "FindMyLoc";
         web:service <https://findmyloc.rww.io/> .
    
    ca:i     cert:key  [
             rdf:type cert:RSAPublicKey;
             cert:exponent 65537;
             cert:modulus "e7414af709522144520617d3531e6099c69932dfd5ec7cc534583d72b71a7d6b202aaa686c74fcc7a5beecbd06a98b24081b36229f2126e448542952c40e02093b30c86035e2bee3d6d0aab38266a1bea863d151c38f53247b8beef61afddf4c57c59841639e691e60145308933d967c59e3c4f23635b2171a46b22e8a4714a44f09d4f825cb0b9b645aaf4c2695ea86f67d3ee721130730375e223b0fc0a36733eb792962e6ebbe79c76cd9e779bb0401eea9a1d4ce3769cb2f1a43331ed4275fef8df64dddd7cfd2ba559b92fcd149b9cb27bb40659ff4b5ce3abc7b30d7980cdc3f8ad8f312d164547509b182298547964a07aa7e99a68e2f797a5631b0f5"^^xsd:hexBinary ] .
"""

  // an implementation of [[LDClient]] that works just for the purpose
  // of this test
  implicit val N3jsLDClient = new LDClient[N3js] {

    val turtleReader = implicitly[RDFReader[N3js, Future, Turtle]]

    def get(url: String): Future[N3js#Graph] = url match {

      case "http://www.w3.org/People/Berners-Lee/card.ttl" =>
        val input = new java.io.StringReader(cardTtl)
        turtleReader.read(input, url)

      case _ =>
        Future.failed(new Exception("unknown url"))

    }

  }

}

import Mocked.N3jsLDClient
import N3jsImplicits.N3jsCollector

object N3jsCollectorTest extends CollectorTest[N3js]
