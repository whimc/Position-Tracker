package edu.whimc.positiontracker.sql.migration.schemas;

import edu.whimc.positiontracker.sql.migration.SchemaVersion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repair migration for installs that were marked as schema 3 without receiving all expected
 * columns (older Schema_3 wrote region yaw against the positions table).
 */
public class Schema_4 extends SchemaVersion {

    public Schema_4() {
        super(4, null);
    }

    @Override
    protected void migrateRoutine(Connection connection) throws SQLException {
        addColumnIfMissing(connection, "whimc_player_positions", "yaw",
                "ALTER TABLE whimc_player_positions ADD COLUMN yaw FLOAT AFTER z");
        addColumnIfMissing(connection, "whimc_player_positions", "pitch",
                "ALTER TABLE whimc_player_positions ADD COLUMN pitch FLOAT AFTER yaw");
        addColumnIfMissing(connection, "whimc_player_positions", "gamemode",
                "ALTER TABLE whimc_player_positions ADD COLUMN gamemode VARCHAR(16) AFTER username");

        addColumnIfMissing(connection, "whimc_player_region_events", "yaw",
                "ALTER TABLE whimc_player_region_events ADD COLUMN yaw FLOAT AFTER z");
        addColumnIfMissing(connection, "whimc_player_region_events", "pitch",
                "ALTER TABLE whimc_player_region_events ADD COLUMN pitch FLOAT AFTER yaw");
        addColumnIfMissing(connection, "whimc_player_region_events", "region_members",
                "ALTER TABLE whimc_player_region_events ADD COLUMN region_members TEXT AFTER region");
    }

    private static void addColumnIfMissing(Connection connection, String table, String column, String alterSql)
            throws SQLException {
        if (columnExists(connection, table, column)) {
            return;
        }
        try (PreparedStatement statement = connection.prepareStatement(alterSql)) {
            statement.execute();
        }
    }

    private static boolean columnExists(Connection connection, String table, String column) throws SQLException {
        String sql = "SELECT 1 FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, table);
            statement.setString(2, column);
            try (ResultSet results = statement.executeQuery()) {
                return results.next();
            }
        }
    }
}
