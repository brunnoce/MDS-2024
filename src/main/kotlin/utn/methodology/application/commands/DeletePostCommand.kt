package utn.methodology.application.commands

class DeletePostCommand(
    val postId: String
) {
    fun validate(): DeletePostCommand {
        checkNotNull(postId) { throw IllegalArgumentException("El ID del post debe estar definido") }
        return this
    }
}