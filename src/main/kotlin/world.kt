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
    fun hexNeighbors(): Array<HexPosition> {
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
    fun vertexNeighbors(): Array<VertexPosition> {
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
}

class Town(val owner: Player, val is_city: Boolean)

class World {
    private val roads: Map<EdgePosition, Player> = HashMap()
    private val towns: Map<VertexPosition, Town> = HashMap()

    fun canPlaceTown(town_pos: VertexPosition, player: Player): Boolean {
        return town_pos.adjacentEdges().any { edge -> roads[edge] == player } && town_pos.adjacentVertices()
            .all { vertex -> !towns.containsKey(vertex) }
    }
}
