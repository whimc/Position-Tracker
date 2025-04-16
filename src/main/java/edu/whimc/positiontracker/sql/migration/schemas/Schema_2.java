package edu.whimc.positiontracker.sql.migration.schemas;

import edu.whimc.positiontracker.sql.migration.SchemaVersion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Schema_2 extends SchemaVersion {

    private static final String ADD_GAMEMODE =
            "ALTER TABLE whimc_player_positions ADD COLUMN gamemode VARCHAR(16) AFTER username;";

    public Schema_2() {
        super(2, new Schema_3());
    }

    @Override
    protected void migrateRoutine(Connection connection) throws SQLException {
        try (PreparedStatement addGamemode = connection.prepareStatement(ADD_GAMEMODE)) {
            addGamemode.execute();
        }
    }
}
