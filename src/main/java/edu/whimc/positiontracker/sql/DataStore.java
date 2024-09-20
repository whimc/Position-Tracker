package edu.whimc.positiontracker.sql;

import edu.whimc.positiontracker.PositionTracker;
import edu.whimc.positiontracker.sql.entries.DataEntry;
import edu.whimc.positiontracker.sql.entries.PositionEntry;
import edu.whimc.positiontracker.sql.entries.RegionEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import org.bukkit.Bukkit;

/**
 * Handles inserting data into the SQL Database.
 */
public class DataStore {

    /**
     * The instance of the plugin.
     */
    private final PositionTracker plugin;
    /**
     * The connection to the SQL database.
     */
    private final MySQLConnection sqlConnection;
    /**
     * Data entries to be inserted into the database.
     */
    private final ConcurrentLinkedQueue<DataEntry> entries = new ConcurrentLinkedQueue<>();
    /**
     * Interval (in ticks) to flush data to the database.
     */
    private final int flushInterval;
    /**
     * Task ID of the BukkitTask for flushing data.
     */
    private int taskId;
    /**
     * Whether the loop to flush data is running.
     */
    private boolean running;

    /**
     * Constructs a DataStore.
     *
     * @param plugin   the instance of the plugin.
     * @param callback the event callback.
     */
    public DataStore(PositionTracker plugin, Consumer<Boolean> callback) {
        this.plugin = plugin;
        this.sqlConnection = new MySQLConnection(plugin);
        this.flushInterval = plugin.getConfig().getInt("flush_interval_seconds", 15) * 20;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final boolean success = this.sqlConnection.initialize();
            Bukkit.getScheduler().runTask(plugin, () -> {
                callback.accept(success);
            });
        });
    }

    /**
     * Start flushing data points to the database. Does nothing if already running
     */
    public void run() {
        if (isRunning()) {
            return;
        }

        this.taskId = Bukkit.getScheduler()
                .runTaskTimerAsynchronously(this.plugin, this::flushToDatabase, this.flushInterval, this.flushInterval)
                .getTaskId();
        this.running = true;
    }

    /**
     * Stop flushing data to the database. This will also prevent the entry queue from receiving new entries.
     */
    public void stop() {
        Bukkit.getScheduler().cancelTask(this.taskId);
        this.running = false;
    }

    /**
     * @return Whether we're writing logs to the database.
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * @param entry Data point to eventually be inserted into the database (if running).
     * @return whether the data point was added.
     */
    public boolean addData(DataEntry entry) {
        if (isRunning()) {
            this.entries.offer(entry);
            return true;
        }
        return false;
    }

    /**
     * Flushes all queued data to the database
     */
    public void flushToDatabase() {
        // Add data to database asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (Connection connection = this.sqlConnection.getConnection()) {
                connection.setAutoCommit(false);
                try (PreparedStatement positions = connection.prepareStatement(PositionEntry.INSERT_QUERY);
                     PreparedStatement regionChanges = connection.prepareStatement(RegionEntry.INSERT_QUERY)) {
                    // Flush all messages from the queue
                    if (!this.entries.isEmpty()) {
                        this.plugin.debugLog("Logging " + this.entries.size() + " data points");
                    }
                    while (!this.entries.isEmpty()) {
                        DataEntry entry = this.entries.poll();

                        // Add entry to appropriate statement
                        if (entry instanceof PositionEntry) {
                            entry.addToStatement(positions);
                        } else if (entry instanceof RegionEntry) {
                            entry.addToStatement(regionChanges);
                        } else {
                            throw new RuntimeException("Unhandled DataEntry type " + entry.getClass().getSimpleName());
                        }
                    }
                    positions.executeBatch();
                    regionChanges.executeBatch();
                }
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
