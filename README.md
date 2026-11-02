# dungeonAdventureG7

Dungeon Adventure, TCSS 360 Group 7 (Alex Douk, John Nguyen, Nathan Chiu, Liiban Isse).

## Requirements

- JDK 17 or later (the SRS targets JDK 21)
- IntelliJ IDEA (or any IDE or command line that can run Java)
- Libraries, included in `lib/` (committed to the repo):
  - `sqlite-jdbc-3.46.1.3.jar` (SQLite driver)
  - `slf4j-api-2.0.13.jar` (logging API required by sqlite-jdbc)
- For running the tests only: JUnit 5. IntelliJ can download it, or download
  `junit-platform-console-standalone-1.10.3.jar` from Maven Central into `lib/`
  (it is git-ignored).

### IntelliJ setup (once per teammate)

1. File > Project Structure > Libraries > + > Java.
2. Select `lib/sqlite-jdbc-3.46.1.3.jar` and `lib/slf4j-api-2.0.13.jar`.
3. Add JUnit 5 when IntelliJ offers it for the test folder.

## Monster database (DAT-2)

`SQLiteDatabase` opens `dungeon.db`, creates the `monster_stats` table and seeds
Ogre, Gremlin and Skeleton from the SRS (section 3.7). The file is created on
first use and is git-ignored.

- `loadMonsterStats(type)` returns a `MonsterStats`
- `insertMonsterStats(type, stats)` adds or replaces a row
- `getAllTypes()` lists the stored monster types
- `SQLiteDatabase.openExisting(path)` opens an existing file only and fails
  with a clear message if it is missing (used at game launch, SRS 3.1)

### Run the tests (from the repo root, Git Bash)

    CP="lib/sqlite-jdbc-3.46.1.3.jar;lib/slf4j-api-2.0.13.jar;lib/junit-platform-console-standalone-1.10.3.jar"
    javac -d out -cp "$CP" src/model/MonsterStats.java src/model/SQLiteDatabase.java test/src/model/SQLiteDatabaseTest.java
    java -jar lib/junit-platform-console-standalone-1.10.3.jar execute -cp "out;lib/sqlite-jdbc-3.46.1.3.jar;lib/slf4j-api-2.0.13.jar" --scan-classpath out
