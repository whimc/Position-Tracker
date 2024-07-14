# WHIMC-PositionTracker
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/whimc/Position-Tracker?label=download&logo=github)](https://github.com/whimc/Position-Tracker/releases/latest)

PositionTracker is a Minecraft plugin that tracks player positions and stores them in an SQL database.

---

## Building
Build the source with Maven:
```
$ mvn install
```

---

## Configuration

`debug` is a `boolean` that toggles debug messages in the console.

### MySQL
| Key	                             | Type	     | Description                          |
|----------------------------------|-----------|--------------------------------------|
| `mysql.host`	                    | `string`  | The host of the database             |
| `mysql.port`	                    | `integer` | The port of the database             |
| `mysql.database`                 | `string`  | The name of the database to use      |
| `mysql.username`                 | `string`  | Username for credentials             |
| `mysql.password`                 | `string`  | Password for credentials             |
| `position_poll_interval_seconds` | `integer` | How often to poll position data      |
| `flush_interval_seconds`         | `integer` | How often to insert data into the db |

#### Example
```yaml
debug: false
mysql:
    host: localhost
    port: 3306
    database: minecraft
    username: user
    password: pass
position_poll_interval_seconds: 2
flush_interval_seconds: 15
```

---

## Commands
| Command	                | Description                          |
|---------------------------|--------------------------------------|
| `/positiontracker status`	| Get the status of the tracker        |
| `/positiontracker debug`  | Toggle the debug messages in console |
| `/positiontracker start`  | Start the tracker                    |
| `/positiontracker stop`   | Stop the tracker                     |

