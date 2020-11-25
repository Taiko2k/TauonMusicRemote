package rocks.tauonmusicbox.tauonremote

class TauonStatus(
        var status: String,
        val shuffle: Boolean,
        val repeat: Boolean,
        val progress: Int,
        val playlist: String,
        val position: Int,
        val album_id: Int,
        val track: TauonTrack
)

class TauonTrack(
        val title: String,
        val artist: String,
        val album: String,
        val album_artist: String,
        val duration: Int,
        val id: Int,
        var position: Int = 0
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
        val id: String
)