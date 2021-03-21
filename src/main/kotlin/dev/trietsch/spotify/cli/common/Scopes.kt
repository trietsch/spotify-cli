package dev.trietsch.spotify.cli.common

object Scopes {
    private const val APP_REMOTE_CONTROL = "app-remote-control"
    private const val USER_READ_PLAYBACK_POSITION = "user-read-playback-position"
    private const val USER_READ_PLAYBACK_STATE = "user-read-playback-state"
    private const val USER_MODIFY_PLAYBACK_STATE = "user-modify-playback-state"
    private const val USER_READ_CURRENTLY_PLAYING = "user-read-currently-playing"
    private const val USER_READ_RECENTLY_PLAYED = "user-read-recently-played"
    private const val USER_TOP_READ = "user-top-read"
    private const val USER_READ_PRIVATE = "user-read-private"
    private const val USER_READ_EMAIL = "user-read-email"
    private const val UGC_IMAGE_UPLOAD = "ugc-image-upload"

    val SCOPES = listOf(
        APP_REMOTE_CONTROL,
        USER_READ_PLAYBACK_POSITION,
        USER_READ_PLAYBACK_STATE,
        USER_MODIFY_PLAYBACK_STATE,
        USER_READ_CURRENTLY_PLAYING,
        USER_READ_RECENTLY_PLAYED,
        USER_TOP_READ,
        USER_READ_PRIVATE,
        USER_READ_EMAIL,
        UGC_IMAGE_UPLOAD,
    ).joinToString(" ")
}
