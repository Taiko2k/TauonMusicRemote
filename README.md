
# Tauon Music Remote

An attempt at making an Andorid remote controller app for the Tauon Music Box desktop app.

App development stage is in alpha and not ready for general use.

## Current target features / design brief

 - Multiple view are presented, **playlists**, **albums**, **tracks** and **now playing**.
 These show content from the users desktop Tauon library.
 - There are two modes of opperation, **remote control**, and **local playback**. 
 Local playback plays audio through the app.
 
## Possible future goals

 - Sync audio for offline playback?
 - Play users local music files?

## Issues / Work needed

 - [Architectural rewrite needed]
    - Move UI data to ViewModel/Livedata
    - Move audio playback to service?
 - Consideration for tablet display sizes?
 - Export playback control notification widget?
 - [Tauon API] Security? Authentication?
 
 ## How to test
 
  - You will need to be using Tauon Music Box v6.4.5 or greater. Enable the setting "Enable server for remote app" in Settings > Function > Page 4.


