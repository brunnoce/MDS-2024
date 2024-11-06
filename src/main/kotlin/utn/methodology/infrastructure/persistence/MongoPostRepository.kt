package utn.methodology.infrastructure.persistence.Repositories

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.types.ObjectId
import utn.methodology.domain.entities.Post
import java.util.*

class MongoPostRepository(private val database: MongoDatabase) {
//ESTO CREO QUE ESTA CORRECTO VIEJO
    private val collection: MongoCollection<Document> = database.getCollection("Posts")

    fun save(post: Post) {
        val document = Document().apply {
            put("_id", post.id.toString()) // Guardar el ID como string UUID
            put("userId", post.userId)
            put("message", post.message)
            put("createdAt", post.createdAt)
        }
        collection.insertOne(document)
    }

    fun findAll(): List<Post> {
        return collection.find().map { document ->
            val id = UUID.fromString(document["_id"].toString())
            val userId = document["userId"] as String
            val message = document["message"] as String
            val createdAt = document["createdAt"] as String // Recupera `createdAt` como String
            Post(id, userId, message, createdAt)
        }.toList()
    }

    fun findByUserIds(userIds: List<String>): List<Post> {
        val filter = Document("userId", Document("\$in", userIds))
        return collection.find(filter).map { document ->
            val id = UUID.fromString(document["_id"].toString())
            val userId = document["userId"] as String
            val message = document["message"] as String
            val createdAt = document["createdAt"] as String
            Post(id, userId, message, createdAt)
        }.toList()
    }

    fun findByUserId(userId: String): List<Post> {
        val filter = Document("userId", userId)
        return collection.find(filter).map { document ->
            val id = UUID.fromString(document["_id"].toString())
            val message = document["message"] as String
            val createdAt = document["createdAt"] as String
            Post(id, userId, message, createdAt)
        }.toList()
    }

    fun deleteById(postId: String) {
        val filter = Document("_id", ObjectId(postId))
        collection.deleteOne(filter)
    }
}
