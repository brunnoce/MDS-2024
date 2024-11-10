package utn.methodology.infrastructure.persistence

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import utn.methodology.domain.contracts.UserRepository
import utn.methodology.domain.entities.User
import java.util.*

class MongoUserRepository(private val database: MongoDatabase) : UserRepository {
    private val collection: MongoCollection<Document> = database.getCollection("users")

    override fun save(user: User) {
        val options = UpdateOptions().upsert(true)
        val filter = Document("_id", user.id)
        val update = Document("\$set", user.toPrimitives())
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
        println("User encontrado: $document")

        return document?.let {
            try {
                User(
                    id = it.getString("_id"),
                    email = it.getString("email"),
                    name = it.getString("name"),
                    password = it.getString("password"),
                    username = it.getString("username"),
                    following = it.getList("following", String::class.java) ?: emptyList(),
                    followers = it.getList("followers", String::class.java) ?: emptyList()
                )
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
            User(
                id = it.getString("_id"),
                email = it.getString("email"),
                name = it.getString("name"),
                password = it.getString("password"),
                username = it.getString("username"),
                following = it.getList("following", String::class.java) ?: emptyList(),
                followers = it.getList("followers", String::class.java) ?: emptyList()
            )
        }
    }

    override fun delete(user: User) {
        val filter = Document("_id", user.id)
        collection.deleteOne(filter)
    }

    fun findAll(): List<User> {
        return collection.find().map {
            User.fromPrimitives(it.toMap() as Map<String, String>)
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
