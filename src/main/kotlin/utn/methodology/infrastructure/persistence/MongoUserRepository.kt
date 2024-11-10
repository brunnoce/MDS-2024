package utn.methodology.infrastructure.persistence

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import org.bson.Document
import utn.methodology.domain.contracts.UserRepository
import utn.methodology.domain.entities.User
import java.util.*

class MongoUserRepository(private val database: MongoDatabase) : UserRepository {
    private val collection: MongoCollection<Document> = database.getCollection("users")

    private val module = SerializersModule {
        contextual(ListSerializer(String.serializer()))
    }

    private val json = Json {
        serializersModule = module
        ignoreUnknownKeys = true
    }

    override fun save(user: User) {
        val options = UpdateOptions().upsert(true)
        val filter = Document("_id", user.id)

        val jsonData = json.encodeToString(User.serializer(), user)
        val document = Document.parse(jsonData)

        val update = Document("\$set", document)
        collection.updateOne(filter, update, options)
    }

    override fun findOne(id: String): User? {
        val uuid = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            return null
        }

        val filter = Document("_id", uuid.toString())
        val document = collection.find(filter).firstOrNull()

        return document?.let {
            try {
                val jsonData = it.toJson()
                json.decodeFromString<User>(jsonData)
            } catch (e: Exception) {
                println("Error al mapear el documento: ${e.localizedMessage}")
                null
            }
        }
    }

    override fun findByUsername(username: String): User? {
        val filter = Document("username", Document("\$regex", "^$username\$").append("\$options", "i"))
        val document = collection.find(filter).firstOrNull()

        return document?.let {
            try {
                val jsonData = it.toJson()
                json.decodeFromString<User>(jsonData)
            } catch (e: Exception) {
                println("Error al mapear el documento: ${e.localizedMessage}")
                null
            }
        }
    }

    override fun delete(user: User) {
        val filter = Document("_id", user.id)
        collection.deleteOne(filter)
    }

    fun findAll(): List<User> {
        return collection.find().mapNotNull {
            try {
                val jsonData = it.toJson()
                json.decodeFromString<User>(jsonData)
            } catch (e: Exception) {
                println("Error al mapear el documento: ${e.localizedMessage}")
                null
            }
        }.toList()
    }

    fun addFollower(userId: String, followerId: String) {
        val filter = Document("_id", userId)
        val update = Document("\$addToSet", Document("followers", followerId))
        collection.updateOne(filter, update)
    }

    fun addFollowing(userId: String, followingId: String) {
        val filter = Document("_id", userId)
        val update = Document("\$addToSet", Document("following", followingId))
        collection.updateOne(filter, update)
    }
}
