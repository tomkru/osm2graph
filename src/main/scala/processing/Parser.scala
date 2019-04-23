package processing

import model.Model._
import scala.io.Source
import scala.xml.XML
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.breakOut

object Parser {
  def parseNodes(path: String): Map[String, Node] = {
    val xml = XML.loadFile(path)
    val maybeIds  = Future(xml \ "node" \\ "@id" map(_.text) toList)
    val maybeLats = Future(xml \ "node" \\ "@lat" map(_.text) toList)
    val maybeLons = Future(xml \ "node" \\ "@lon" map(_.text) toList)

    val ids = Await.result(maybeIds, 5 minute)
    val lats = Await.result(maybeLats, 5 minute)
    val lons = Await.result(maybeLons, 5 minute)

    ids.zip(lats).zip(lons).map(x => (x._1._1 -> Node(x._1._1, x._1._2.toDouble, x._2.toDouble)))(breakOut) 
  }

  def parseWays(path: String): List[Way] = {
    val xml = XML.loadFile(path)
    val maybeIds: Future[List[String]] = Future(xml \ "way" \\ "@id" map(_.text) toList)
    val maybeNodeStrings: Future[List[List[String]]] = 
      Future(xml \\ "way" map(x => x \\ "nd" \\ "@ref") map(nodeSeq => nodeSeq.map(_.text) toList) toList)

    val allNodes = parseNodes(path)
    val ways = for {
      ids <- maybeIds
      nodeStrings <- maybeNodeStrings
    } yield ids.zip(nodeStrings).par.map(x => nodesToWay(x._1, x._2, allNodes)).toList
    Await.result(ways, 5 minute)
  }

  private def nodesToWay(id: String, nodes: List[String], allNodes: Map[String, Node]): Way = {
    val parsedNodes: List[Node] = nodes.map(node => allNodes(node))
    Way(id, true, parsedNodes)
  }

  def way2Edges(way: Way): List[Edge] = 
    way.nodes.sliding(2,1).toList.flatMap(e => List(Edge(way.id, e.head, e.last), Edge(way.id, e.last, e.head))) 
  
  def ways2Edges(ways: List[Way]): List[Edge] = ways.flatMap(way2Edges(_))

  def edges2Graph(edges: List[Edge]): Map[Node, Map[Node, Double]] = {
    edges.foldLeft(Map[Node, Map[Node, Double]]()){ case (acc, i) => {
        if(acc contains i.orig) {
          val innerMap = acc(i.orig) + (i.dest -> i.weight)
          acc + (i.orig -> innerMap)
        } else {
          val innerMap = Map(i.dest -> i.weight)
          acc + (i.orig -> innerMap)
        }
        
        
      //acc + (i.orig -> (i.dest -> i.weight))
      }
    }
  }
}
