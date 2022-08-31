package juuxel.cucuwiki

enum class PostAction {
    UPDATE,
    ;

    companion object {
        fun byName(name: String): PostAction? =
            values().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
}
