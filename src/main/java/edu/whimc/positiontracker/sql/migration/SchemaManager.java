package edu.whimc.positiontracker.sql.migration;

import edu.whimc.positiontracker.PositionTracker;
import edu.whimc.positiontracker.sql.MySQLConnection;
import edu.whimc.positiontracker.sql.migration.schemas.Schema_1;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;

public class SchemaManager {

    public static final String VERSION_FILE_NAME = ".schema_version";

    private static final SchemaVersion BASE_SCHEMA = new Schema_1();

    private final PositionTracker plugin;

    private final MySQLConnection connection;

    public SchemaManager(PositionTracker plugin, MySQLConnection connection) {
        this.plugin = plugin;
        this.connection = connection;
    }

    protected Connection getConnection() {
        try {
            return this.connection.getConnection();
        } catch (SQLException e) {
            return null;
        }
    }

    protected File getVersionFile() {
        return new File(this.plugin.getDataFolder(), VERSION_FILE_NAME);
    }

    private int getCurrentVersion() {
        try {
            return Integer.parseInt(new String(Files.readAllBytes(getVersionFile().toPath())));
        } catch (NumberFormatException | IOException exc) {
            return 0;
        }
    }

    public boolean initialize() {
        int curVersion = getCurrentVersion();

        SchemaVersion schema = BASE_SCHEMA;
        while (schema != null) {
            if (schema.getVersion() > curVersion) {
                this.plugin.getLogger().info("Migrating to schema " + schema.getVersion() + "...");
                if (!schema.migrate(this)) {
                    this.plugin.getLogger().severe("Migration to schema " + schema.getVersion() + " failed.");
                    return false;
                } else {
                    this.plugin.getLogger().info("Migration to schema " + schema.getVersion() + " completed.");
                }
            } else {
                this.plugin.getLogger().info("Skipping schema " + schema.getVersion() + ", already applied.");
            }
            schema = schema.getNextSchema();
        }

        return true;
    }

}
