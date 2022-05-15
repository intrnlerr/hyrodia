package com.hyrodia

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(WebSockets)
        routing {
            val openLobbies = ArrayList<Lobby>()
            webSocket("/") {
                println("player joined")
                val me = Player(this)
                // TODO: implement some way to create a lobby or join an existing lobby
                if (openLobbies.isEmpty()) {
                    val lobby = Lobby(me)
                    openLobbies.add(lobby)
                } else {
                    openLobbies.first().players.add(me)
                }
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val packet = Json.decodeFromString<GamePacket>(frame.readText())
                        when (packet.action) {
                            "start" -> {
                                val lobby = openLobbies.first()
                                if (lobby.isLeader(me)) {
                                    println("game started")
                                    openLobbies.removeFirst()
                                    lobby.create()
                                }
                            }
                            // FIXME: race conditions are likely here (: this should probably using channels
                            "rollDice" -> {
                                me.game?.doDiceRoll(me)
                            }
                            "endTurn" -> {
                                me.game?.endTurn(me)
                            }
                            "placeCity" -> {
                                packet.cityPos?.run { me.game?.tryPlaceCity(me, this) }
                            }
                        }
                    }
                }
                println("player leave")
                if (me.game == null) {
                    openLobbies.first().players.remove(me)
                }
            }
        }
    }.start(wait = true)
}

@Serializable
data class GamePacket(val action: String, val cityPos: VertexPosition? = null, val roadPos: EdgePosition? = null)

class Lobby(leader: Player) {
    val players: MutableSet<Player> = HashSet()

    init {
        players.add(leader)
    }

    fun isLeader(player: Player): Boolean = player == players.first()
    fun create(): Game {
        val game = Game(players.toTypedArray())
        for (player in game.players) {
            player.game = game
        }
        return game
    }
}
