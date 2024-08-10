package no.ludjan.translationTest.translation

@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FIELD
)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExtractForTranslation(
    val key: String,
    val description: String,
    val defaultValue: String,
    val sourceLanguage: String,
)
