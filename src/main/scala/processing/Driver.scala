package processing

import model.Model._
import processing.Parser._
import graph.Dijkstra._

object Driver {
  def findPathInMap(path: String, source: String, destination: String, edgesToDeactivate: List[(String, String)] = List(), wayToDeactivate: Option[String] = None) = {
    val nodes = parseNodes(path)
    val sourceNode = nodes(source)
    val destNode = nodes(destination)

    val waysO = parseWays(path)
    val ways = if(wayToDeactivate.isEmpty) waysO else deactivateWays(waysO)(wayToDeactivate.get).filter(_.active)
    val allEdges = ways2Edges(ways)
    val edgesO = allEdges.filter(_.active)
    val edges = if(edgesToDeactivate.size == 0) edgesO else deactivateEdges(edgesO)(edgesToDeactivate).filter(_.active)
    val graph = edges2Graph(edges)

    val graphF: Graph[Node] = (x: Node) => graph(x)
    val nodesInPath = shortestPath(graphF)(sourceNode, destNode).getOrElse(List())
    nodesInPath
    //nodesInPath.sliding(2, 1).toList.foldLeft(0.0){ (acc: Double, i) => acc + Edge("", i.head, i.last).weight }
  }

  def deactivateEdge(edges: List[Edge])(source: String, destination: String) = {
    edges.filter(e => e.orig.id != source || e.dest.id != destination) ++ edges.filter(e => e.orig.id == source && e.dest.id == destination).map(e => e.copy(active = false))
  }

  def deactivateEdges(edges: List[Edge])(tuples: List[(String, String)]): List[Edge] = {
    tuples.flatMap(tuple => deactivateEdge(edges)(tuple._1, tuple._2))
  }

  def deactivateWays(ways: List[Way])(wayId: String) = {
    ways.filter(_.id != wayId) ++ ways.filter(_.id == wayId).map(_.copy(active = false))
  }

}

