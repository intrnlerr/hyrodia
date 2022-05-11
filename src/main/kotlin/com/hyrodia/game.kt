package com.hyrodia

import kotlin.random.Random

class Game(val players: Array<Player>) {
    val world = World()
    var turnNumber: Int = 0
    var inSetupPhase = false

    fun doDiceRoll(who: Player) {
        if (players[turnNumber] != who) {
            return
        }
        val roll = Random.nextInt(1, 7) + Random.nextInt(1, 7)
        if (roll == 7) {
            // TODO: move the thief & steal resources
        } else {
            world.updatePlayerResources(roll)
        }
    }

    suspend fun tryPlaceCity(who: Player, where: VertexPosition) {
        if (players[turnNumber] != who) {
            return
        }
        if (inSetupPhase) {
            // TODO: deal with this
            return
        }
        if (world.canPlaceTown(where, who)) {
            world.placeTown(where, who)
            for (player in players) {
                player.send(GamePacket("townPlaced", cityPos = where))
            }
        }
    }

    fun endTurn(who: Player) {
        if (players[turnNumber] != who) {
            return
        }
        turnNumber = (turnNumber + 1) % players.size
    }

}