package utn.methodology.application.commands

data class FollowUserCommand(val followerId: String, val followingId: String) {
    fun validate() {
        if (followerId == followingId) {
            throw IllegalArgumentException("No puedes seguirte a ti mismo.")
        }
    }
}
