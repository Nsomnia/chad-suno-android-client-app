package dev.nsomnia.chadsuno.data.remote

import com.google.gson.*
import dev.nsomnia.chadsuno.domain.model.SongStatus
import java.lang.reflect.Type

class SongStatusDeserializer : JsonDeserializer<SongStatus> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SongStatus {
        return SongStatus.fromString(json.asString)
    }
}

class SongStatusSerializer : JsonSerializer<SongStatus> {
    override fun serialize(src: SongStatus, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.name.lowercase())
    }
}
