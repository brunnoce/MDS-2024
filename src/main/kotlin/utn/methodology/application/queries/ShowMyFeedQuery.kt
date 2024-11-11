package utn.methodology.application.queries

data class ShowMyFeedQuery(val userId: String) {

    fun validate(): ShowMyFeedQuery {
        check(userId.isNotBlank()) { "UserId debe estar definido y no vacío" }
        return this
    }
}
