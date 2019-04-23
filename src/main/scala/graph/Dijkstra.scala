package graph

import de.ummels.prioritymap.PriorityMap

object Dijkstra {
  type Graph[N] = N => Map[N, Double]

  def shortestPath[N](g: Graph[N])(source: N, target: N): Option[List[N]] = {
    val pred = dijkstra(g)(source)._2
    if (pred.contains(target) || source == target)
      Some(iterateRight(target)(pred.get))
    else None
  }

  def iterateRight[N](x: N)(f: N => Option[N]): List[N] = {
    def go(x: N, acc: List[N]): List[N] = f(x) match {
      case None => x :: acc
      case Some(y) => go(y, x :: acc)
    }

    go(x, List.empty)
  }

  def dijkstra[N](g: Graph[N])(source: N): (Map[N, Double], Map[N, N]) = {
    def go(active: PriorityMap[N, Double], res: Map[N, Double], pred: Map[N, N]): (Map[N, Double], Map[N, N]) =
      if (active.isEmpty) (res, pred)
      else {
        val (node, cost) = active.head
        val neighbours = for {
          (n, c) <- g(node) if !res.contains(n) &&
            cost + c < active.getOrElse(n, Double.MaxValue)
        } yield n -> (cost + c)
        val preds = neighbours mapValues (_ => node)
        go(active.tail ++ neighbours, res + (node -> cost), pred ++ preds)
      }

    go(PriorityMap(source -> 0), Map.empty, Map.empty)
  }
}
