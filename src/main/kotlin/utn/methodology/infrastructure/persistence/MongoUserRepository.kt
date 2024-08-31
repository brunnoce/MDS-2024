package utn.methodology.infrastructure.persistence

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import utn.methodology.domain.entities.User

class MongoUserRepository(private val database: MongoDatabase) {
    private var collection: MongoCollection<Any>;

    init {
        collection = database.getCollection("users") as MongoCollection<Any>;
    }

    fun save(user: User) {
        val options = UpdateOptions().upsert(true);

        val filter = Document("_id", user.getId()) // Usa el campo id como filter
        val update = Document("\$set", user.toPrimitives())

        collection.updateOne(filter, update, options)
    }

    fun findOne(id: String): User? {
        val filter = Document("_id", id);

        val primitives = collection.find(filter).firstOrNull();

        if (primitives == null) {
            return null;
        }

        return User.fromPrimitives(primitives as Map<String, String>)
    }

    fun findAll(): List<User> {

        val primitives = collection.find().map { it as Document }.toList();

        return primitives.map {
            User.fromPrimitives(it.toMap() as Map<String, String>)
        };
    }

    fun delete(user: User) {
        val filter = Document("_id", user.getId());

        collection.deleteOne(filter)
    }

}