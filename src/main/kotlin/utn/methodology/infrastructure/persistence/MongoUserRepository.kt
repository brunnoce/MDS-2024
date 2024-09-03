package utn.methodology.infrastructure.persistence

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import utn.methodology.domain.entities.User
import com.mongodb.client.model.Filters.eq

class MongoUserRepository(private val database: MongoDatabase) {
    private val collection: MongoCollection<Document> = database.getCollection("users")

    fun save(user: User) {
        val options = UpdateOptions().upsert(true)

        val filter = Document("_id", user.getId()) // Usa el campo id como filtro
        val update = Document("\$set", user.toPrimitives())

        collection.updateOne(filter, update, options)
    }

    fun findOne(id: String): User? {
        val filter = Document("_id", id)
        val document = collection.find(filter).firstOrNull()

        return document?.let {
            User.fromPrimitives(it.toMap() as Map<String, String>)
        }
    }

    fun findAll(): List<User> {
        val documents = collection.find().toList()

        return documents.map {
            User.fromPrimitives(it.toMap() as Map<String, String>)
        }
    }

    fun findByUsername(username: String): User? {
        val filter = Document("username", username)
        val document = collection.find(filter).firstOrNull()

        return document?.let {
            User.fromPrimitives(it.toMap() as Map<String, String>)
        }
    }

    fun delete(user: User) {
        val filter = Document("_id", user.getId())
        collection.deleteOne(filter)
    }
}
