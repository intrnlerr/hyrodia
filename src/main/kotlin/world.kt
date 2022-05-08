import kotlin.random.Random

enum class ResourceType {
    Sheep,
    Lumber,
    Brick,
    Ore,
    Grain
}

class Tile(val resource_type: ResourceType, val roll: Int)

fun getTile(x: Int, y: Int): Tile {
    val random = Random(x * y)
    val roll = intArrayOf(2, 3, 4, 5, 6, 8, 9, 10, 11, 12).random(random)
    val resource = ResourceType.values().random(random)
    return Tile(resource, roll)
}
