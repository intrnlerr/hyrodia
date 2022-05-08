import kotlin.random.Random

enum class ResourceType {
    Sheep, Lumber, Brick, Ore, Grain
}

class Tile(val resource_type: ResourceType, val roll: Int)

fun getTile(x: Int, y: Int): Tile {
    val random = Random(x * y)
    val roll = intArrayOf(2, 3, 4, 5, 6, 8, 9, 10, 11, 12).random(random)
    val resource = ResourceType.values().random(random)
    return Tile(resource, roll)
}

data class HexPosition(val x: Int, val y: Int) {
    fun northWest() = HexPosition(x - 1, y + 1)
    fun northEast() = HexPosition(x, y + 1)
    fun southWest() = HexPosition(x, y - 1)
    fun southEast() = HexPosition(x + 1, y - 1)
    fun east() = HexPosition(x + 1, y)
    fun west() = HexPosition(x - 1, y)
}

enum class VertexType {
    Top, Bottom,
}

data class VertexPosition(val position: HexPosition, val vertexType: VertexType) {
    fun adjacentHexagons(): Array<HexPosition> {
        return when (vertexType) {
            VertexType.Top -> arrayOf(position, position.northEast(), position.northWest())
            VertexType.Bottom -> arrayOf(position, position.southEast(), position.southWest())
        }
    }

    fun adjacentEdges(): Array<EdgePosition> {
        return when (vertexType) {
            VertexType.Top -> arrayOf(
                EdgePosition(position, EdgeType.NorthWest),
                EdgePosition(position, EdgeType.NorthEast),
                EdgePosition(position.northEast(), EdgeType.West),
            )
            VertexType.Bottom -> arrayOf(
                EdgePosition(position.southWest(), EdgeType.NorthEast),
                EdgePosition(position.southEast(), EdgeType.NorthWest),
                EdgePosition(position.southEast(), EdgeType.West),
            )
        }
    }

    fun adjacentVertices(): Array<VertexPosition> {
        return when (vertexType) {
            VertexType.Top -> arrayOf(
                VertexPosition(position.northWest(), VertexType.Bottom),
                VertexPosition(position.northEast(), VertexType.Bottom),
                VertexPosition(HexPosition(position.x - 1, position.y + 2), VertexType.Bottom)
            )
            VertexType.Bottom -> arrayOf(
                VertexPosition(position.southWest(), VertexType.Top),
                VertexPosition(position.southEast(), VertexType.Top),
                VertexPosition(HexPosition(position.x + 1, position.y - 2), VertexType.Top)
            )
        }
    }
}

enum class EdgeType {
    West, NorthWest, NorthEast
}

data class EdgePosition(val position: HexPosition, val edgeType: EdgeType) {
    fun adjacentVertices(): Array<VertexPosition> {
        return when (edgeType) {
            EdgeType.West -> arrayOf(
                VertexPosition(position.northWest(), VertexType.Bottom),
                VertexPosition(position.southWest(), VertexType.Top)
            )
            EdgeType.NorthWest -> arrayOf(
                VertexPosition(position, VertexType.Top), VertexPosition(position.northWest(), VertexType.Bottom)
            )
            EdgeType.NorthEast -> arrayOf(
                VertexPosition(position, VertexType.Top), VertexPosition(position.northEast(), VertexType.Bottom)
            )
        }
    }

    fun adjacentEdges(): Array<EdgePosition> {
        return when (edgeType) {
            EdgeType.West -> arrayOf(
                EdgePosition(position, EdgeType.NorthWest),
                EdgePosition(position.west(), EdgeType.NorthEast),
                EdgePosition(position.southWest(), EdgeType.NorthEast),
                EdgePosition(position.southWest(), EdgeType.NorthWest),
            )
            EdgeType.NorthWest -> arrayOf(
                EdgePosition(position, EdgeType.West),
                EdgePosition(position, EdgeType.NorthEast),
                EdgePosition(position.west(), EdgeType.NorthEast),
                EdgePosition(position.northEast(), EdgeType.West),
            )
            EdgeType.NorthEast -> arrayOf(
                EdgePosition(position, EdgeType.NorthWest),
                EdgePosition(position.east(), EdgeType.West),
                EdgePosition(position.east(), EdgeType.NorthWest),
                EdgePosition(position.northEast(), EdgeType.West),
            )
        }
    }
}

class Town(val owner: Player, val is_city: Boolean)

class World {
    private val roads: Map<EdgePosition, Player> = HashMap()
    private val towns: Map<VertexPosition, Town> = HashMap()

    fun canPlaceTown(town_pos: VertexPosition, player: Player): Boolean {
        return town_pos.adjacentEdges().any { edge -> roads[edge] == player } && town_pos.adjacentVertices()
            .all { vertex -> !towns.containsKey(vertex) }
    }

    fun canPlaceRoad(road_pos: EdgePosition, player: Player): Boolean {
        return !roads.containsKey(road_pos) && (road_pos.adjacentEdges()
            .any { edge -> roads[edge] == player } || road_pos.adjacentVertices()
            .any { vertex -> towns[vertex]?.owner == player })
    }

    fun updatePlayerResources(roll: Int) {
        for ((pos, town) in towns) {
            for ((x, y) in pos.adjacentHexagons()) {
                val tile = getTile(x, y)
                if (tile.roll == roll) {
                    val amount = if (town.is_city) 2u else 1u
                    town.owner.resources.compute(tile.resource_type) { _, num -> (num ?: 0u) + amount }
                }
            }
        }
    }
}
