package utn.methodology.infrastructure.persistence.Repositories

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.types.ObjectId
import utn.methodology.domain.entities.Post
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class MongoPostRepository(private val database: MongoDatabase) {

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
        try {
            return collection.find().map { document ->
                // Convertimos el _id de MongoDB a UUID, si es posible
                val id = try {
                    UUID.fromString(document["_id"]?.toString() ?: "")
                } catch (e: IllegalArgumentException) {
                    UUID.randomUUID() // Generamos un UUID por defecto si la conversión falla
                }

                // Convertimos el resto de los campos a String
                val userId = document["userId"]?.toString() ?: ""
                val message = document["message"]?.toString() ?: ""
                val createdAt = document["createdAt"]?.toString() ?: LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)

                // Creamos y devolvemos el objeto Post
                Post(id, userId, message, createdAt)
            }.toList()
        } catch (e: Exception) {
            println("Error al recuperar posts: ${e.message}")
            throw e
        }
    }

    fun findByUserId(userId: String): List<Post> {
        try {
            val query = Document("userId", userId)
            return collection.find(query).map { document ->
                val id = UUID.fromString(document["_id"].toString()) // Convertir _id a UUID
                val userId = document["userId"] as String
                val message = document["message"] as String
                val createdAt = document["createdAt"] as? String ?: LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)

                Post(id, userId, message, createdAt)
            }.toList()
        } catch (e: Exception) {
            println("Error al buscar posts por userId: ${e.message}")
            throw e
        }
    }

    fun findById(postId: String): Post? {
        val filter = Document("_id", postId)
        val document = collection.find(filter).firstOrNull()

        return document?.let {
            val id = UUID.fromString(it["_id"].toString())
            val userId = it["userId"] as String
            val message = it["message"] as String
            val createdAt = it["createdAt"] as String
            Post(id, userId, message, createdAt)
        }
    }

    fun deleteById(postId: String) {
        val filter = Document("_id", postId)
        val result = collection.deleteOne(filter)
        if (result.deletedCount == 0L) {
            throw Exception("No se encontró el post con ID: $postId")
        }
    }
}
