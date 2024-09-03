package utn.methodology.application.queries

data class FindUserByUsernameQuery(
    val username: String
) {

    fun validate(): FindUserByUsernameQuery {
        checkNotNull(username) {throw IllegalArgumentException("Username debe esta definido")}
        return this
    }
}