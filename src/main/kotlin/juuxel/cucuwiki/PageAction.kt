package juuxel.cucuwiki

enum class PageAction {
    VIEW, EDIT,
    ;

    companion object {
        fun byName(name: String): PageAction? =
            values().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
}
