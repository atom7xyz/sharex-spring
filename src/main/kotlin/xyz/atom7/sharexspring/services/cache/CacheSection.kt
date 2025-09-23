package xyz.atom7.sharexspring.services.cache

enum class CacheSection(val value: String) {
    PROFILE("profile"),
    FILE_UPLOAD("file_upload"),

    // hash -> id
    FILE_UPLOAD_HASH("file_upload_hash"),

    // path -> id
    FILE_UPLOAD_PATH("file_upload_path"),

    // token -> id
    FILE_UPLOAD_TOKENS("file_upload_tokens"),

    SHORTENED_URL("shortened_url"),
    API_KEY("api_key")
}