package edu.whimc.positiontracker.sql.migration.schemas;

import edu.whimc.positiontracker.sql.migration.SchemaVersion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Schema_3 extends SchemaVersion {

    // Add to whimc_player_positions
    private static final String ADD_POS_YAW =
            "ALTER TABLE whimc_player_positions ADD COLUMN yaw FLOAT AFTER z;";
    private static final String ADD_POS_PITCH =
            "ALTER TABLE whimc_player_positions ADD COLUMN pitch FLOAT AFTER yaw;";

    // Add to whimc_player_region_events
    private static final String ADD_REGION_YAW =
            "ALTER TABLE whimc_player_region_events ADD COLUMN yaw FLOAT AFTER z;";
    private static final String ADD_REGION_PITCH =
            "ALTER TABLE whimc_player_region_events ADD COLUMN pitch FLOAT AFTER yaw;";
    private static final String ADD_REGION_MEMBERS =
            "ALTER TABLE whimc_player_region_events ADD COLUMN region_members VARCHAR(64) AFTER region;";

    public Schema_3() {
        super(3, null);  // No newer schema after this one (yet)
    }

    @Override
    protected void migrateRoutine(Connection connection) throws SQLException {
        // Update player_positions table
        try (PreparedStatement addYaw = connection.prepareStatement(ADD_POS_YAW)) {
            addYaw.execute();
        }
        try (PreparedStatement addPitch = connection.prepareStatement(ADD_POS_PITCH)) {
            addPitch.execute();
        }

        // Update player_region_events table
        try (PreparedStatement addYaw = connection.prepareStatement(ADD_POS_YAW)) {
            addYaw.execute();
            System.out.println("✓ Adding Yaw, Pitch and Region Members");
        } catch (SQLException e) {
            System.err.println("✗ Could not add to whimc_player_positions: " + e.getMessage());
        }

        try (PreparedStatement addRegionPitch = connection.prepareStatement(ADD_REGION_PITCH)) {
            addRegionPitch.execute();
        }
        try (PreparedStatement addRegionMembers = connection.prepareStatement(ADD_REGION_MEMBERS)) {
            addRegionMembers.execute();
        }
    }
}
