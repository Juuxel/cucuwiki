package juuxel.cucuwiki.config

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Comment(val comment: String)
