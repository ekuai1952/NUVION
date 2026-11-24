package kz.nuvion.app.domain.model

/** Interface languages. Kazakh is the default; its strings live in res/values. */
enum class AppLanguage(val tag: String, val label: String) {
    KK("kk", "KK"),
    RU("ru", "RU"),
    EN("en", "EN");

    companion object {
        val Default = KK
        fun fromTag(tag: String?): AppLanguage = entries.firstOrNull { it.tag == tag } ?: Default
    }
}

/**
 * Learning content (lessons, questions, problems) comes from the repository, not from string
 * resources, so every piece of content carries all three translations.
 */
data class LocalizedText(val kk: String, val ru: String, val en: String) {
    fun resolve(language: AppLanguage): String = when (language) {
        AppLanguage.KK -> kk
        AppLanguage.RU -> ru
        AppLanguage.EN -> en
    }

    companion object {
        /** Text that is identical in all languages, e.g. a formula. */
        fun same(text: String) = LocalizedText(text, text, text)
        val Empty = same("")
    }
}
