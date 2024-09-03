package utn.methodology.application.queries

data class FindUserByUsernameQuery(
    val username: String
) {

    fun validate(): FindUserByUsernameQuery {
        checkNotNull(username) {throw IllegalArgumentException("Username must be defined")}
        return this
    }
}