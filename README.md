# BoatRace
Adds a new way to race your friends!\
Spigot 1.18/1.19 - by GuitarXpress (HeyImJ0hn)

###### /boatrace | /btr

###### Player Commands:
- /btr help - Shows useful commands
- /btr join <track> - Joins lobby for specified track
- /btr leave - Leaves current track/lobby
- /btr tracks - Shows available tracks

###### Admin Commands:
- /btr add <name> <laps> - Adds new track
- /btr remove <track> - Removes specified track
- /btr setlobby - Sets general lobby (There is no lobby for each track, just one for all)
- /btr edit <track> - Toggles edit mode for specified track
- /btr setstatus <track> <status> -> Sets track state/status (setup | joinable | starting | ongoing | cancelled | ended | unavailable)

###### Permission nodes:
- "br.admin" - Access to admin commands
- "br.status" - Access to /btr setstatus
- "br.use" - Access to join & leave
- "br.signs" - Access to plugin sign criation (join & leave signs)

###### Track Setup:
To setup a new track simply use **/btr add <name of track> <nr of laps>**.\
After using the command I advise you to set the checkpoints before anything else. The checkpoints are only used to make sure the player follows the right path for the track.\
For the first checkpoint you need to place **white wool** 1 block under the track. So if the track is on **y**, the wool should be on **y - 1**.\
The second checkpoint follows the same rule but with **black wool**. The finish line also follows this rule but **bedrock** is used.\
After setting the checkpoints and finish line you'll need to set the spawns and the boundaries of said track with **/btr edit <track>** .\

##### Sign Setup:
To create game signs, simply place a sign and write the following:\
Keep in mind that each line here represents a line on the sign and _empty_ represents an empty line.
- [boatrace]
- «track name»
- _empty_
- _empty_

To create the leave sign write the following:
- [boatrace]
- _empty_
- leave
- _empty_

###### Additional Info:
Finish line -> **Bedrock**\
Checkpoint one -> **White Wool**\
Checkpoint two -> **Black Wool**
