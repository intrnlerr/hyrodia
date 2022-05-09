import kotlin.random.Random

class Game(val players: Array<Player>) {
    val world = World()
    var turnNumber: Int = 0
    var inSetupPhase = true

    fun doDiceRoll() {
        val roll = Random.nextInt(1, 7) + Random.nextInt(1, 7)
        if (roll == 7) {
            // TODO: move the thief & steal resources
        } else {
            world.updatePlayerResources(roll)
        }
    }


}