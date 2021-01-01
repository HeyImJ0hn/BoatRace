# BoatRace
Adds a new way to race your friends!\
Spigot 1.16 - by GuitarXpress (HeyImJ0hn)

###### /boatrace | /btr

###### Player Commands:
- /btr help -> Shows useful commands
- /btr join <track> -> Joins lobby for specified track
- /btr leave -> Leaves current track/lobby

###### Admin Commands:
- /btr add <name> <laps> -> Adds new track
- /btr remove <track> -> Removes specified track
- /btr setlobby -> Sets general lobby (There is no lobby for each track, just one for all)
- /btr setspawns <track> -> Gives item to start setting spawnpoints for specified track
- /btr setbounds <track> -> Gives item to start setting boundaries for specified track
- /btr setstate <track> <state> -> Sets track state/status (setup | joinable | starting | ongoing | cancelled | ended | unavailable)

###### Permission nodes:
- "br.admin" -> Access to admin commands
- "br.state" -> Access to /btr setstate 
- "br.use" -> Access to join & leave
- "br.signs" -> Access to plugin sign criation (join & leave signs)

###### Track Setup:
To setup a new track simply use /btr add <name of track> <nr of laps>.\
After using the command I advise you to set the checkpoints before anything else. The checkpoints are only used to make sure the player follows the right path for the track.\
For the first checkpoint you need to place **white wool** 1 block under the track. So if the track is on **y**, the wool should be on **y - 1**.\
The second checkpoint follows the same rule but with **black wool**. The finish line also follows this rule but **bedrock** is used.\
After setting the checkpoints and finish line you'll need to set the spawns and the boundaries of said track with /btr setspawns <track> and /btr setbounds <track>.\
Simply follow the instructions after executing each command.\

###### Additional Info:
Finish line -> **Bedrock**\
Checkpoint one -> **White Wool**\
Checkpoint two -> **Black Wool**
