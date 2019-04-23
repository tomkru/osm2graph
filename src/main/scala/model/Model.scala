package model

import scala.math._

object Model {
  case class Node(
    id: String,
    lat: Double,
    lon: Double)

  case class Way(
    id: String,
    active: Boolean,
    nodes: List[Node]
    ) {
      def weight: Double = {
        val first = nodes.head
        val last = nodes.last
        getDistanceFromLatLonInKm(first.lat, first.lon, last.lat, last.lon) }
    }

  case class Edge(
    wayId: String,
    orig: Node,
    dest: Node,
    active: Boolean = true) {
      def weight: Double = getDistanceFromLatLonInKm(orig.lat, orig.lon, dest.lat, dest.lon)
    }

  private def getDistanceFromLatLonInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double) = {
    val R = 6371 // Radius of the earth in km
    val dLat = deg2rad(lat2-lat1)
    val dLon = deg2rad(lon2-lon1) 
    val a = 
      sin(dLat/2) * sin(dLat/2) +
      cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * 
      sin(dLon/2) * sin(dLon/2) 
    val c = 2 * atan2(sqrt(a), sqrt(1-a)) 
    R * c 
  }

  private def deg2rad(deg: Double) =  {
    deg * (Math.PI/180)
  }
}
