package utn.methodology.application.queries

data class FindUserByIdQuery(
    val id: String
) {
    fun validate(): FindUserByIdQuery {
        checkNotNull(id) {throw IllegalArgumentException("Username debe esta definido")}
        return this
    }
}