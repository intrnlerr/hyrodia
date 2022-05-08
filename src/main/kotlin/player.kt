import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Player {
    var cards : ArrayList<Card> = ArrayList()
    var resources : EnumMap<ResourceType, UInt> = EnumMap(ResourceType::class.java)
    var towns : ArrayList<Town> = ArrayList()


}