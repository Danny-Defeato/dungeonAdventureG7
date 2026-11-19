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

/**
 * Manages the SQLite database that stores monster statistics. On creation it
 * opens (or creates) the database file, creates the {@code monster_stats}
 * table if needed and fills it with the default Ogre, Gremlin and Skeleton
 * rows when the table is empty. All queries use {@link PreparedStatement}.
 *
 * @author Group 7 (Alex Douk)
 * @version 0.2
 */
public class SQLiteDatabase implements AutoCloseable {

    /** The default database location, relative to the project folder. */
    public static final String DEFAULT_URL = "jdbc:sqlite:dungeon.db";

    /** The start of every JDBC url for a SQLite file. */
    private static final String URL_PREFIX = "jdbc:sqlite:";

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

    private static final String OGRE_TYPE = "Ogre";

    private static final String GREMLIN_TYPE = "Gremlin";

    private static final String SKELETON_TYPE = "Skeleton";

    /** The default Ogre statistics (SRS 3.7). */
    private static final MonsterStats DEFAULT_OGRE =
            new MonsterStats(200, 30, 60, 2, 0.6, 0.1, 30, 60);

    /** The default Gremlin statistics (SRS 3.7). */
    private static final MonsterStats DEFAULT_GREMLIN =
            new MonsterStats(70, 15, 30, 5, 0.8, 0.4, 20, 40);

    /** The default Skeleton statistics (SRS 3.7). */
    private static final MonsterStats DEFAULT_SKELETON =
            new MonsterStats(100, 30, 50, 3, 0.8, 0.3, 30, 50);

    /** The JDBC url of the database. */
    private final String myUrl;

    /** The open connection. */
    private Connection myConnection;

    /**
     * Opens the database at the given JDBC url, creating the table and the
     * default monster rows if they do not exist yet.
     *
     * @param theUrl a JDBC url such as {@code jdbc:sqlite:dungeon.db}
     * @throws IllegalArgumentException if the url is null
     * @throws IllegalStateException if the database cannot be opened or set up
     */
    public SQLiteDatabase(final String theUrl) {
        if (theUrl == null) {
            throw new IllegalArgumentException("url must not be null");
        }
        myUrl = theUrl;
        try {
            myConnection = DriverManager.getConnection(theUrl);
            try (Statement st = myConnection.createStatement()) {
                st.execute(CREATE_SQL);
            }
            seedDefaultsIfEmpty();
        } catch (SQLException e) {
            close();
            throw new IllegalStateException("Could not open database at " + theUrl, e);
        }
    }

    /**
     * Opens a database file that must already exist, as the game does at
     * launch (SRS 3.1). Unlike the constructor, this never creates a new file.
     *
     * @param theFile the path of the database file, such as {@code dungeon.db}
     * @return the opened database
     * @throws IllegalArgumentException if the path is null
     * @throws IllegalStateException if the file is missing, unreadable, or not
     *         a usable SQLite database; the message names the absolute path
     */
    public static SQLiteDatabase openExisting(final Path theFile) {
        if (theFile == null) {
            throw new IllegalArgumentException("file must not be null");
        }
        final Path abs = theFile.toAbsolutePath();
        if (!Files.isRegularFile(abs) || !Files.isReadable(abs)) {
            throw new IllegalStateException(
                    "Monster database not found or unreadable: " + abs);
        }
        return new SQLiteDatabase(URL_PREFIX + abs);
    }

    /**
     * Returns the names of every monster type stored in the database.
     *
     * @return the type names in alphabetical order, never null
     * @throws IllegalStateException if the database operation fails
     */
    public List<String> getAllTypes() {
        final List<String> types = new ArrayList<>();
        try (Statement st = myConnection.createStatement();
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
        return myUrl;
    }

    /**
     * Stores the statistics for a monster type, replacing any existing row
     * with the same type.
     *
     * @param theType the monster type name, such as "Ogre"; not null or blank
     * @param theStats the statistics to store, not null
     * @throws IllegalArgumentException if the type is blank or the stats are null
     * @throws IllegalStateException if the database operation fails
     */
    public void insertMonsterStats(final String theType, final MonsterStats theStats) {
        requireType(theType);
        if (theStats == null) {
            throw new IllegalArgumentException("stats must not be null");
        }
        try (PreparedStatement ps = myConnection.prepareStatement(INSERT_SQL)) {
            ps.setString(1, theType);
            ps.setInt(2, theStats.getHitPoints());
            ps.setInt(3, theStats.getMinDamage());
            ps.setInt(4, theStats.getMaxDamage());
            ps.setInt(5, theStats.getAttackSpeed());
            ps.setDouble(6, theStats.getChanceToHit());
            ps.setDouble(7, theStats.getChanceToHeal());
            ps.setInt(8, theStats.getMinHeal());
            ps.setInt(9, theStats.getMaxHeal());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not store " + theType, e);
        }
    }

    /**
     * Loads the statistics for a monster type.
     *
     * @param theType the monster type name, such as "Ogre"
     * @return the stored statistics
     * @throws IllegalArgumentException if type is blank or no row exists for it
     * @throws IllegalStateException if the database operation fails
     */
    public MonsterStats loadMonsterStats(final String theType) {
        requireType(theType);
        try (PreparedStatement ps = myConnection.prepareStatement(SELECT_SQL)) {
            ps.setString(1, theType);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("No monster type: " + theType);
                }
                return new MonsterStats(rs.getInt("hitPoints"),
                        rs.getInt("minDamage"), rs.getInt("maxDamage"),
                        rs.getInt("attackSpeed"), rs.getDouble("chanceToHit"),
                        rs.getDouble("chanceToHeal"), rs.getInt("minHeal"),
                        rs.getInt("maxHeal"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load " + theType, e);
        }
    }

    /**
     * Closes the database connection. Safe to call more than once.
     */
    @Override
    public void close() {
        if (myConnection != null) {
            try {
                myConnection.close();
            } catch (SQLException e) {
                // nothing useful to do; the connection is being discarded
            }
            myConnection = null;
        }
    }

    private void seedDefaultsIfEmpty() throws SQLException {
        try (Statement st = myConnection.createStatement();
             ResultSet rs = st.executeQuery(COUNT_SQL)) {
            if (rs.next() && rs.getInt(1) == 0) {
                insertMonsterStats(OGRE_TYPE, DEFAULT_OGRE);
                insertMonsterStats(GREMLIN_TYPE, DEFAULT_GREMLIN);
                insertMonsterStats(SKELETON_TYPE, DEFAULT_SKELETON);
            }
        }
    }

    private static void requireType(final String theType) {
        if (theType == null || theType.isBlank()) {
            throw new IllegalArgumentException("type must not be blank");
        }
    }
}
