package com.hyrodia

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.collections.ArrayList

class Player(private val connection: DefaultWebSocketServerSession) {
    suspend fun send(packet: GamePacket) {
        connection.send(Frame.Text(Json.encodeToString(packet)))
    }

    var game: Game? = null

    var cards : ArrayList<Card> = ArrayList()
    var resources : EnumMap<ResourceType, UInt> = EnumMap(ResourceType::class.java)
    var towns : ArrayList<Town> = ArrayList()


}