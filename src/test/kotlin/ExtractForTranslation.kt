@Target(AnnotationTarget.PROPERTY, AnnotationTarget.LOCAL_VARIABLE)
@Retention(AnnotationRetention.SOURCE)
annotation class ExtractForTranslation(
    val context: String = "",
    val key: String = "",
    val description: String = "",
    val sourceLanguage: String = "en"
)
