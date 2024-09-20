package edu.whimc.positiontracker.sql.entries;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class DataEntry {
    public abstract void addToStatement(PreparedStatement statement) throws SQLException;
}
