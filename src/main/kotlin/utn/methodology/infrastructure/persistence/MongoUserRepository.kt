package utn.methodology.infrastructure.persistence

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import utn.methodology.domain.entities.User
import com.mongodb.client.model.Filters
import org.bson.conversions.Bson
import kotlin.collections.emptyList

class MongoUserRepository(private val database: MongoDatabase) {
    private val collection: MongoCollection<Document> = database.getCollection("users")

    fun save(user: User) {
        val options = UpdateOptions().upsert(true)
        val filter = Document("_id", user.id)
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


    fun findByUsernameContains(username: String): List<Document> {
        val filter: Bson = Filters.regex("username", ".*${Regex.escape(username)}.*", "i")
        return collection.find(filter).toList()
    }

    fun findAll(): List<User> {
        val documents = collection.find().toList()
        return documents.map {
            User.fromPrimitives(it.toMap() as Map<String, String>)
        }
    }

    fun findByUsername(username: String): User? {
        val filter = Document("username", Document("\$regex", "^$username").append("\$options", "i"))
        val document = collection.find(filter).firstOrNull()
        return document?.let {
            User.fromPrimitives(it.toMap() as Map<String, String>)
        }
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

    fun removeFollower(userId: String, followerId: String) {
        val filter = Document("_id", userId)
        val update = Document("\$pull", Document("followers", followerId))
        collection.updateOne(filter, update)
    }

    fun removeFollowing(userId: String, followingId: String) {
        val filter = Document("_id", userId)
        val update = Document("\$pull", Document("following", followingId))
        collection.updateOne(filter, update)
    }

    fun findFollowers(userId: String): List<User> {
        val user = findOne(userId) ?: return emptyList()
        val followersIds = user.followers
        return followersIds.mapNotNull { findOne(it) }
    }

    // Encuentra los usuarios a los que un usuario sigue
    fun findFollowing(userId: String): List<User> {
        val user = findOne(userId) ?: return emptyList()
        val followingIds = user.following
        return followingIds.mapNotNull { findOne(it) }
    }
}
