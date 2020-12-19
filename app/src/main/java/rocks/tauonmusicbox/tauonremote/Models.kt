package rocks.tauonmusicbox.tauonremote

class TauonVersion(var version: Int)

class TauonStatus(
        var status: String,
        val shuffle: Boolean,
        val repeat: Boolean,
        var progress: Int,
        val playlist: String,
        var playlist_length: Int,
        var position: Int,
        var album_id: Int,
        var track: TauonTrack
)

class TauonTrack(
        val title: String = "",
        val artist: String = "",
        val album: String = "",
        val album_artist: String = "",
        val duration: Int = 0,
        val id: Int = -1,
        val position: Int = 0,
        val album_id: Int = -1,
        val track_number: String = ""
)

class TrackListData(
        val tracks: List<TauonTrack>
)

class PlaylistData(
        val playlists: List<TauonPlaylist>
)

class AlbumData(
        val albums: List<TauonTrack>
)

class TauonPlaylist(
        val name: String,
        val id: String,
        val count: Int
)