package utn.methodology.domain.entities

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer


// Define un SerializersModule para registrar los tipos polimórficos
val module = SerializersModule {
    contextual(ListSerializer(String.serializer())) // Registrar el serializer para List<String>
}

// Configura Json para usar el módulo
val json = Json {
    serializersModule = module
    ignoreUnknownKeys = true
}
