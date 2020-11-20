package co.brainly.onesky.client

data class LanguageListResponse(
    val data: List<Language>
)

data class Language(
    val code: String,
    val english_name: String,
    val is_base_language: Boolean,
    val translation_progress: String
)
