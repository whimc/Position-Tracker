# WHIMC-PositionTracker
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/whimc/Position-Tracker?label=download&logo=github)](https://github.com/whimc/Position-Tracker/releases/latest)

Tracks online player positions to a database.

Tracker starts when a player joins the server (bringing player count from 0 to 1), and stops when the last online player logs out.

Defaults to tracking player location (world, biome, block) - support for tracking player mouse position being worked on.

Player data is queried every 3 seconds and stored in a database.

## Building
Build the source with Maven:
```
$ mvn install
```

## Commands

**/positiontracker** - Brings up help menu with list of commands

**/positiontracker help** - Brings up help menu - alias for /positiontracker

**/positiontracker status** - Prints status of tracker (on/off) 

**/positiontracker debug** - Enables/disables debug messages in the server console 

**/positiontracker start** - Manually starts the tracker 

**/positiontracker stop** - Manually stops the tracker 