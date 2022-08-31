package juuxel.cucuwiki

enum class GetAction {
    VIEW,
    EDIT,
    ;

    companion object {
        fun byName(name: String): GetAction? =
            values().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
}
