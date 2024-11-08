package utn.methodology.infrastructure.persistence

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import utn.methodology.domain.contracts.UserRepository
import utn.methodology.domain.entities.User
import com.mongodb.client.model.Filters
import org.bson.conversions.Bson

class MongoUserRepository(private val database: MongoDatabase) : UserRepository {
    private val collection: MongoCollection<Document> = database.getCollection("users")

    override fun save(user: User) {
        val options = UpdateOptions().upsert(true)
        val filter = Document("_id", user.id)
        val update = Document("\$set", user.toPrimitives())
        collection.updateOne(filter, update, options)
    }

    override fun findOne(id: String): User? {
        val filter = Document("_id", id)
        val document = collection.find(filter).firstOrNull()
        println("Documento encontrado: $document")

        return document?.let {
            try {
                // Extraer los valores de cada campo con su tipo correspondiente
                User(
                    id = it.getString("_id"),
                    email = it.getString("email"),
                    name = it.getString("name"),
                    password = it.getString("password"),
                    username = it.getString("username"),
                    following = it.getList("following", String::class.java),
                    followers = it.getList("followers", String::class.java)
                )
            } catch (e: Exception) {
                println("Error al mapear el documento: ${e.localizedMessage}")
                null
            }
        }
    }


    override fun findByUsername(username: String): User? {
        val filter = Document("username", Document("\$regex", "^$username").append("\$options", "i"))
        val document = collection.find(filter).firstOrNull()
        return document?.let {
            User.fromPrimitives(it.toMap() as Map<String, String>)
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

    fun findByUsernameContains(username: String): List<Document> {
        val filter: Bson = Filters.regex("username", ".*${Regex.escape(username)}.*", "i")
        return collection.find(filter).toList()
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
