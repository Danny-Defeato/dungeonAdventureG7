package src.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Manages the SQLite database that stores monster statistics. On creation it
 * opens (or creates) the database file, creates the {@code monster_stats}
 * table if needed and fills it with the default Ogre, Gremlin and Skeleton
 * rows when the table is empty. All queries use {@link PreparedStatement}.
 *
 * @author Group 7 (Alex Douk)
 * @version 0.1
 */
public class SQLiteDatabase implements AutoCloseable {

    /** The default database location, relative to the project folder. */
    public static final String DEFAULT_URL = "jdbc:sqlite:dungeon.db";

    private static final String CREATE_SQL =
            "CREATE TABLE IF NOT EXISTS monster_stats ("
            + "type TEXT PRIMARY KEY NOT NULL, "
            + "hitPoints INTEGER NOT NULL, "
            + "minDamage INTEGER NOT NULL, "
            + "maxDamage INTEGER NOT NULL, "
            + "attackSpeed INTEGER NOT NULL, "
            + "chanceToHit REAL NOT NULL, "
            + "chanceToHeal REAL NOT NULL, "
            + "minHeal INTEGER NOT NULL, "
            + "maxHeal INTEGER NOT NULL)";

    private static final String INSERT_SQL =
            "INSERT OR REPLACE INTO monster_stats (type, hitPoints, minDamage, "
            + "maxDamage, attackSpeed, chanceToHit, chanceToHeal, minHeal, "
            + "maxHeal) VALUES (?,?,?,?,?,?,?,?,?)";

    private static final String SELECT_SQL =
            "SELECT * FROM monster_stats WHERE type = ?";

    private static final String COUNT_SQL = "SELECT COUNT(*) FROM monster_stats";

    private static final String TYPES_SQL =
            "SELECT type FROM monster_stats ORDER BY type";

    /** The JDBC url of the database. */
    private final String url;

    /** The open connection. */
    private Connection connection;

    /**
     * Opens the database at the given JDBC url, creating the table and the
     * default monster rows if they do not exist yet.
     *
     * @param url a JDBC url such as {@code jdbc:sqlite:dungeon.db}
     * @throws IllegalStateException if the database cannot be opened or set up
     */
    public SQLiteDatabase(final String url) {
        this.url = Objects.requireNonNull(url, "url");
        try {
            connection = DriverManager.getConnection(url);
            try (Statement st = connection.createStatement()) {
                st.execute(CREATE_SQL);
            }
            seedDefaultsIfEmpty();
        } catch (SQLException e) {
            close();
            throw new IllegalStateException("Could not open database at " + url, e);
        }
    }

    /**
     * Opens a database file that must already exist, as the game does at
     * launch (SRS 3.1). Unlike the constructor, this never creates a new file.
     *
     * @param file the path of the database file, such as {@code dungeon.db}
     * @return the opened database
     * @throws IllegalStateException if the file is missing, unreadable, or not
     *         a usable SQLite database; the message names the absolute path
     */
    public static SQLiteDatabase openExisting(final Path file) {
        Objects.requireNonNull(file, "file");
        final Path abs = file.toAbsolutePath();
        if (!Files.isRegularFile(abs) || !Files.isReadable(abs)) {
            throw new IllegalStateException(
                    "Monster database not found or unreadable: " + abs);
        }
        return new SQLiteDatabase("jdbc:sqlite:" + abs);
    }

    /**
     * Returns the names of every monster type stored in the database.
     *
     * @return the type names in alphabetical order, never null
     * @throws IllegalStateException if the database operation fails
     */
    public List<String> getAllTypes() {
        final List<String> types = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(TYPES_SQL)) {
            while (rs.next()) {
                types.add(rs.getString("type"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not list monster types", e);
        }
        return types;
    }

    /**
     * Returns the JDBC url this database was opened with.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Stores the statistics for a monster type, replacing any existing row
     * with the same type.
     *
     * @param type the monster type name, such as "Ogre"; not null or blank
     * @param stats the statistics to store, not null
     * @throws IllegalArgumentException if type is blank
     * @throws IllegalStateException if the database operation fails
     */
    public void insertMonsterStats(final String type, final MonsterStats stats) {
        requireType(type);
        Objects.requireNonNull(stats, "stats");
        try (PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            ps.setString(1, type);
            ps.setInt(2, stats.getHitPoints());
            ps.setInt(3, stats.getMinDamage());
            ps.setInt(4, stats.getMaxDamage());
            ps.setInt(5, stats.getAttackSpeed());
            ps.setDouble(6, stats.getChanceToHit());
            ps.setDouble(7, stats.getChanceToHeal());
            ps.setInt(8, stats.getMinHeal());
            ps.setInt(9, stats.getMaxHeal());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not store " + type, e);
        }
    }

    /**
     * Loads the statistics for a monster type.
     *
     * @param type the monster type name, such as "Ogre"
     * @return the stored statistics
     * @throws IllegalArgumentException if type is blank or no row exists for it
     * @throws IllegalStateException if the database operation fails
     */
    public MonsterStats loadMonsterStats(final String type) {
        requireType(type);
        try (PreparedStatement ps = connection.prepareStatement(SELECT_SQL)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("No monster type: " + type);
                }
                return new MonsterStats(rs.getInt("hitPoints"),
                        rs.getInt("minDamage"), rs.getInt("maxDamage"),
                        rs.getInt("attackSpeed"), rs.getDouble("chanceToHit"),
                        rs.getDouble("chanceToHeal"), rs.getInt("minHeal"),
                        rs.getInt("maxHeal"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load " + type, e);
        }
    }

    /**
     * Closes the database connection. Safe to call more than once.
     */
    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // nothing useful to do; the connection is being discarded
            }
            connection = null;
        }
    }

    private void seedDefaultsIfEmpty() throws SQLException {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(COUNT_SQL)) {
            if (rs.next() && rs.getInt(1) == 0) {
                insertMonsterStats("Ogre",
                        new MonsterStats(200, 30, 60, 2, 0.6, 0.1, 30, 60));
                insertMonsterStats("Gremlin",
                        new MonsterStats(70, 15, 30, 5, 0.8, 0.4, 20, 40));
                insertMonsterStats("Skeleton",
                        new MonsterStats(100, 30, 50, 3, 0.8, 0.3, 30, 50));
            }
        }
    }

    private static void requireType(final String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type must not be blank");
        }
    }
}
