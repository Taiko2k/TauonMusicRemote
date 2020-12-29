
# Tauon Music Remote

An attempt at making an Andorid remote controller app for [Tauon Music Box](https://github.com/Taiko2k/TauonMusicBox). Its currently alpha quality with various bugs, but you may still find it useful in its current state.

<img width="220" src="https://user-images.githubusercontent.com/17271572/102763775-070d1d00-43df-11eb-8df6-b4dd4c854f31.jpg">

### Feratures

 - Navigate your playlists and albums.
 - **Remote control** playback of Tauon Music Box.
 - Play your library on your device by **streaming** from Tauon Music Box. 
 
 
### Possible future goals

 - Sync audio for offline playback?
 - Play users local music files?


## How to use

- Download and install the latest Tauon Music Remote [APK v0.5](https://github.com/Taiko2k/TauonMusicRemote/releases/download/0.5/TauonMusicRemoteAlpha5.apk).
- In Tauon Music Box, Enable the setting "Enable server for remote app" in Settings > Function > Page 4.
- Find your PC's local IP address (try `ip a`) and enter in app. Then if both apps are running you should see Tauon Music Remote then populated with content.

### Tip

- Tap the track title at the bottom of the screen to "go to playing".

___


## Known issues

 - App will crash when screen is rotated
 - Need to restart app if playlist changes were made on server

### Work needed

 - [Architectural rewrite needed]
    - Move UI data to ViewModel/Livedata
    - Move audio playback to service?
 - Reload data when server data chagnes
 - Consideration for tablet display sizes?
 - Export playback control notification widget?
 - [Tauon API] Security? Authentication?
 - [Tauon API] Advanced search? Playlist generation?
