package utn.methodology.infrastructure.http.router

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import utn.methodology.application.commandhandlers.CreatePostHandler
import utn.methodology.application.commandhandlers.PostValidationException
import utn.methodology.infrastructure.persistence.Repositories.MongoPostRepository
import utn.methodology.infrastructure.persistence.connectToMongoDB
import utn.methodology.infrastructure.persistence.MongoUserRepository

fun Application.postRouter() {
    val mongoDatabase = connectToMongoDB() // Conexión a la base de datos
    val postRepository = MongoPostRepository(mongoDatabase) // Repositorio de posts
    val userRepository = MongoUserRepository(mongoDatabase) // Repositorio de usuarios

    val createPostHandler = CreatePostHandler(postRepository)
    routing {
        post("/posts") {
            try {
                println("Recibiendo solicitud para crear un post...")
                val request = call.receive<PostRequest>()
                println("Cuerpo de la solicitud: $request")

                if (userRepository.findOne(request.userId) != null) {
                    val post = createPostHandler.createPost(request.userId, request.message)
                    println("Post creado: $post")
                    call.respond(HttpStatusCode.Created, mapOf("message" to "ok"))
                } else {
                    println("No se encontró el usuario, por lo que no se puede agregar un post")
                    call.respond(HttpStatusCode.BadRequest, "Usuario no encontrado")
                }

            } catch (e: PostValidationException) {
                println("Error de validación del post: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Datos de post inválidos")
            } catch (e: Exception) {
                println("Error inesperado: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Error al procesar la solicitud")
            }
        }

        get("/posts") {
            val userId = call.request.queryParameters["userId"]
            val order = call.request.queryParameters["order"] ?: "ASC"
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0

            if (userId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "El parámetro 'userId' es requerido.")
                return@get
            }

            try {
                val posts = postRepository.findByUserId(userId)
                val filteredPosts = when (order.uppercase()) {
                    "DESC" -> posts.sortedByDescending { it.createdAt }.drop(offset).take(limit)
                    else -> posts.sortedBy { it.createdAt }.drop(offset).take(limit)
                }
                call.respond(HttpStatusCode.OK, filteredPosts)
            } catch (ex: Exception) {
                println("Error al procesar la solicitud de obtener posts: ${ex.message}")
                call.respond(HttpStatusCode.InternalServerError, "Error al procesar la solicitud")
            }
        }

        delete("/posts/{id}") {
            val postId = call.parameters["id"]

            if (postId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "El ID del post es requerido.")
                return@delete
            }

            try {
                createPostHandler.deletePost(postId)
                call.respond(HttpStatusCode.NoContent)
            } catch (ex: Exception) {
                println("Error al eliminar el post: ${ex.message}")
                call.respond(HttpStatusCode.InternalServerError, "Error al procesar la solicitud")
            }
        }
    }
}

@Serializable
data class PostRequest(val userId: String, val message: String)
