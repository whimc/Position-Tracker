package edu.whimc.positiontracker.sql.migration;

import com.google.common.io.Files;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class SchemaVersion {

    private final int version;
    private final SchemaVersion nextSchema;

    protected SchemaVersion(int version, SchemaVersion next) {
        this.version = version;
        this.nextSchema = next;
    }

    public int getVersion() {
        return this.version;
    }

    public SchemaVersion getNextSchema() {
        return this.nextSchema;
    }

    protected abstract void migrateRoutine(Connection connection) throws SQLException;

    public final boolean migrate(SchemaManager manager) {
        // Migrate the database
        try {
            migrateRoutine(manager.getConnection());
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        }

        // Update the schema version
        try {
            Files.write(String.valueOf(this.version).getBytes(), manager.getVersionFile());
        } catch (IOException exc) {
            exc.printStackTrace();
            return false;
        }

        return true;
    }

}
