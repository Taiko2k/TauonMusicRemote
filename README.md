
# Tauon Music Remote

An attempt at making an Andorid remote controller app for [Tauon Music Box](https://github.com/Taiko2k/TauonMusicBox). Its currently alpha quality, but you may find it useful.

<img width="220" src="https://user-images.githubusercontent.com/17271572/102763775-070d1d00-43df-11eb-8df6-b4dd4c854f31.jpg">

## Current target features / design brief

 - There are two modes of opperation, **remote control** and **streaming**. 
 - Multiple view are presented, **playlists**, **albums**, **tracks** and **now playing**.

## Issues / Work needed

 - [Architectural rewrite needed]
    - Move UI data to ViewModel/Livedata
    - Move audio playback to service?
 - Reload data when server data chagnes
 - Consideration for tablet display sizes?
 - Export playback control notification widget?
 - [Tauon API] Security? Authentication?
 - [Tauon API] Advanced search? Playlist generation?
 
 
## Possible future goals

 - Sync audio for offline playback?
 - Play users local music files?


# How to use

- See releases section for an APK.
- You will need to be using Tauon Music Box v6.4.5 or greater. Enable the setting "Enable server for remote app" in Settings > Function > Page 4.
- Find your PC's local IP address (try `ip a`) and enter in app. Then if both apps are running you should see the app populated with content.

## Tips

- Tap the track title at the bottom of the screen to "go to playing".
