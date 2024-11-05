package edu.whimc.positiontracker.sql.migration.schemas;

import edu.whimc.positiontracker.sql.migration.SchemaVersion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Schema_1 extends SchemaVersion {

    /** Table that will store player positions. */
    private static final String CREATE_POSITIONS_TABLE =
            "CREATE TABLE IF NOT EXISTS `whimc_player_positions` (" +
                    "  `rowid`    BIGINT AUTO_INCREMENT NOT NULL," +
                    "  `x`        INT                   NOT NULL," +
                    "  `y`        INT                   NOT NULL," +
                    "  `z`        INT                   NOT NULL," +
                    "  `world`    VARCHAR(64)           NOT NULL," +
                    "  `biome`    VARCHAR(64)           NOT NULL," +
                    "  `username` VARCHAR(16)           NOT NULL," +
                    "  `uuid`     VARCHAR(36)           NOT NULL," +
                    "  `time` 	  BIGINT                NOT NULL," +
                    "  PRIMARY KEY (`rowid`));";

    /** Table that will store region change events. */
    private static final String CREATE_REGIONS_TABLE =
            "CREATE TABLE IF NOT EXISTS `whimc_player_region_events` (" +
                    "  `rowid`    BIGINT AUTO_INCREMENT NOT NULL," +
                    "  `region`   VARCHAR(64) NOT NULL," +
                    "  `trigger`  VARCHAR(16) NOT NULL," +
                    "  `isEnter`  BIT(1) NOT NULL," +
                    "  `x`        INT                   NOT NULL," +
                    "  `y`        INT                   NOT NULL," +
                    "  `z`        INT                   NOT NULL," +
                    "  `world`    VARCHAR(64)           NOT NULL," +
                    "  `username` VARCHAR(16)           NOT NULL," +
                    "  `uuid`     VARCHAR(36)           NOT NULL," +
                    "  `time` 	  BIGINT                NOT NULL," +
                    "  PRIMARY KEY (`rowid`));";


    public Schema_1() {
        super(1, new Schema_2());
    }

    @Override
    protected void migrateRoutine(Connection connection) throws SQLException {
        try (PreparedStatement createPosTable = connection.prepareStatement(CREATE_POSITIONS_TABLE)) {
            createPosTable.execute();
        }
        try (PreparedStatement createRegionTable = connection.prepareStatement(CREATE_REGIONS_TABLE)) {
            createRegionTable.execute();
        }
    }
}
