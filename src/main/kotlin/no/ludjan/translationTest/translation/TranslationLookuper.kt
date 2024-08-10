package no.ludjan.translationTest.translation

object TranslationLookuper {

    val resourceFileNames = listOf("translated/no.properties")
    private const val DEFAULT_DEFAULT_VALUE = "SuperDefault"

    fun lookup(key: String, language: String): String? {
        val fileName = "/translated/$language.properties"
        val file = this::class.java.getResourceAsStream(fileName)?.bufferedReader() ?: throw IllegalStateException("Oops")
        val lines = file.readLines()
        var i = 0
        while (i < lines.size) {
            if (lines[i].startsWith("Key") && lines[i].endsWith(key)) {
                return lines[i+4].split(":").last()
            }
            i++
        }
        return null
    }
}