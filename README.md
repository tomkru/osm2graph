# osm2graph

## Installation
```
git clone https://github.com/tomkru/osm2graph.git
cd osm2graph/
./sbt
console
```

1. Clone the repository to your local computer
2. `cd` into the created directory
3. Run the sbt runner
4. Start the Scala REPL


## Usage

Once inside the Scala REPL, first import the driver with `import processing.Driver._`

Now you can call the `findPathInMap` function which takes the following parameters:
* `path: String` - path to an osm file
* `source: String` - ID of the source node
* `destination: String` - ID of the destination node
* `edgesToDeactivate: List[(String, String)]` - edges to deactivate represented as a tuple of strings, ids of the source and destination nodes of the edge
* `wayToDeactivate: Option[String]` - ID of way to deactivate wrapped in an option

Examples:

* `findPathInMap("map.osm", "123", "456") //edgesToDeactivate default to empty list, wayToDeactivate default to None` - finds the path from node 123 to node 456
* `findPathInMap("map.osm", "123", "456", List("1" -> "2", "3" -> "4"), Some("789"))` - finds the path from node 123 to  node 456, ignoring edges 1->2, 3->4 and way with id 789
* `findPathInMap("map.osm", "123", "456", List("1" -> "2", "3" -> "4"))` - finds the path from node 123 to  node 456, ignoring edges 1->2 and 3->4
* `findPathInMap("map.osm", "123", "456", List(), Some("789"))` finds the path from node 123 to  node 456, ignoring way with id 789
