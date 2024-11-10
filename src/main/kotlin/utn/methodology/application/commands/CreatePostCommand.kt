package utn.methodology.application.commands

data class CreatePostCommand(
    val userId: String,
    val message: String
) {
    fun validate(): CreatePostCommand {
        checkNotNull(userId) { throw IllegalArgumentException("UserID no debe ser nulo") }
        checkNotNull(message) { throw IllegalArgumentException("Username no debe ser nulo") }

        return this;
    }
}