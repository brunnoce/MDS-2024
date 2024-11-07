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

    fun findPostsByUserIds(userIds: List<String>): List<Post> {
        // Verifica que la lista de userIds no esté vacía
        if (userIds.isEmpty()) {
            println("No hay usuarios seguidos para la consulta.")
            return emptyList()  // Retorna una lista vacía si no hay seguidores
        }

        val filter = Document("userId", Document("\$in", userIds))  // Filtrar por userId
        val sort = Document("createdAt", -1)  // Ordenar por fecha de creación (descendente)
        println("Consulta MongoDB - Filtro: $filter")
        val posts = collection.find(filter).sort(sort).toList()

        // Verifica los posts obtenidos
        println("Posts obtenidos: $posts")

        return posts.map { document ->
            // Conversión de documento a objeto Post
            val postMap = document.toMap() as Map<String, String>
            Post.fromPrimitives(postMap)
        }
    }


    fun save(post: Post) {
        val document = Document().apply {
            put("_id", post.id.toString())
            put("userId", post.userId)
            put("message", post.message)
            put("createdAt", post.createdAt)
        }
        collection.insertOne(document)
    }

    fun findAll(): List<Post> {
        return collection.find().map { document ->
            val id = try {
                UUID.fromString(document["_id"]?.toString() ?: "")
            } catch (e: IllegalArgumentException) {
                UUID.randomUUID()
            }
            val userId = document["userId"]?.toString() ?: ""
            val message = document["message"]?.toString() ?: ""
            val createdAt = document["createdAt"]?.toString() ?: LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            Post(id, userId, message, createdAt)
        }.toList()
    }

    fun findByUserId(userId: String): List<Post> {
        val query = Document("userId", userId)
        return collection.find(query).map { document ->
            val id = UUID.fromString(document["_id"].toString())
            val userId = document["userId"] as String
            val message = document["message"] as String
            val createdAt = document["createdAt"] as? String ?: LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            Post(id, userId, message, createdAt)
        }.toList()
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


