package utn.methodology.infrastructure.persistence.Repositories

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import io.ktor.server.plugins.*
import org.bson.Document
import utn.methodology.domain.contracts.PostRepository
import utn.methodology.domain.entities.Post
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MongoPostRepository(private val database: MongoDatabase) : PostRepository {

    private val collection: MongoCollection<Document> = database.getCollection("Posts")

    override fun save(post: Post) {
        val document = Document().apply {
            put("_id", post.id.toString())
            put("userId", post.userId)
            put("message", post.message)
            put("createdAt", post.createdAt)
        }
        collection.insertOne(document)
    }

    override fun findByOwnerId(ownerId: String): List<Post> {
        val query = Document("userId", ownerId)
        return collection.find(query).map { document ->
            val id = UUID.fromString(document["_id"].toString())
            val userId = document["userId"] as String
            val message = document["message"] as String
            val createdAt = document["createdAt"] as String
            Post(id, userId, message, createdAt)
        }.toList()
    }

    override fun findByFollows(followIds: List<String>): List<Post> {
        val filter = Document("userId", Document("\$in", followIds))
        return collection.find(filter).map { document ->
            val id = UUID.fromString(document["_id"].toString())
            val userId = document["userId"] as String
            val message = document["message"] as String
            val createdAt = document["createdAt"] as String
            Post(id, userId, message, createdAt)
        }.toList()
    }

    fun findAll(order: String, limit: Int, offset: Int): List<Post> {
        val sortOrder = if (order == "ASC") 1 else -1
        val sort = Document("createdAt", sortOrder)

        println("Ejecutando consulta con orden: $order, limit: $limit, offset: $offset")

        val result = collection.find()
            .sort(sort)
            .skip(offset)
            .limit(limit)
            .map { document ->
                val id = try {
                    UUID.fromString(document["_id"]?.toString() ?: "")
                } catch (e: IllegalArgumentException) {
                    UUID.randomUUID()
                }
                val userId = document["userId"]?.toString() ?: ""
                val message = document["message"]?.toString() ?: ""
                val createdAt = document["createdAt"]?.toString() ?: LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                Post(id, userId, message, createdAt)
            }
            .toList()

        println("Resultados encontrados: ${result.size}")
        return result
    }

    override fun findById(postId: String): Post? {
        val filter = Document("_id", postId)
        val document = collection.find(filter).firstOrNull()
        return document?.let {
            val id = UUID.fromString(it["_id"].toString())
            val userId = it["userId"] as String
            val message = it["message"] as String
            val createdAt = it["createdAt"] as String
            Post(id, userId, message, createdAt)
        } ?: throw NotFoundException("Post no encontrado con ID: $postId")
    }

    override fun deleteById(postId: String) {
        val filter = Document("_id", postId)
        val result = collection.deleteOne(filter)
        if (result.deletedCount == 0L) {
            throw Exception("No se encontró el post con ID: $postId")
        }
    }

    override fun findPostsByUserIds(userIds: List<String>): List<Post> {
        if (userIds.isEmpty()) {
            println("No hay usuarios seguidos para la consulta.")
            return emptyList()
        }

        val filter = Document("userId", Document("\$in", userIds))
        val sort = Document("createdAt", -1)
        println("Consulta MongoDB - Filtro: $filter")

        return collection.find(filter).sort(sort).map { document ->
            val id = UUID.fromString(document["_id"].toString())
            val userId = document["userId"] as String
            val message = document["message"] as String
            val createdAt = document["createdAt"] as String
            Post(id, userId, message, createdAt)
        }.toList()
    }

}
