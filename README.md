# dungeonAdventureG7

Dungeon Adventure, TCSS 360 Group 7 (Alex Douk, John Nguyen, Nathan Chiu, Liiban Isse).

## Monster database (DAT-2)

`SQLiteDatabase` opens `dungeon.db`, creates the `monster_stats` table and seeds
Ogre, Gremlin and Skeleton from the SRS (section 3.7). `loadMonsterStats(type)`
returns a `MonsterStats`.

### Dependencies

Jars go in `lib/` (they are git-ignored, so download them once):

- sqlite-jdbc 3.46.1.3
- slf4j-api 2.0.13
- junit-platform-console-standalone 1.10.3

All three are on Maven Central. In IntelliJ, add the folder as a library.

### Run the tests (from the repo root, Git Bash)

    CP="lib/sqlite-jdbc-3.46.1.3.jar;lib/slf4j-api-2.0.13.jar;lib/junit-platform-console-standalone-1.10.3.jar"
    javac -d out -cp "$CP" src/model/MonsterStats.java src/model/SQLiteDatabase.java test/src/model/SQLiteDatabaseTest.java
    java -jar lib/junit-platform-console-standalone-1.10.3.jar execute -cp "out;lib/sqlite-jdbc-3.46.1.3.jar;lib/slf4j-api-2.0.13.jar" --scan-classpath out
