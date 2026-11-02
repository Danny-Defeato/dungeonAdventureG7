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

# Group 7 Iteration 1 (Oct 26 - Nov 1, 2026)

TEAM MEMBERS
Alex Douk, John Nguyen, Nathan Chiu, Liiban Isse

Alex Douk

WORK COMPLETED
- DAT-2 Set up SQLite Database (7 hours actual): MonsterStats class, SQLiteDatabase
  class (monster_stats table, seeded with Ogre/Gremlin/Skeleton from the SRS,
  insert and load methods), JUnit tests, and repo setup (README, jars in lib/).
- DAT-12 Add monster type listing and missing-database handling (2 hours):
  getAllTypes() and openExisting(Path) with tests (20 JUnit tests total, all passing).
- Merged to main through pull requests #2, #3 and #5 from Alex_branch.
- Total: 9 hours.

NOTE ON LOGGED TIME
The YouTrack time for DAT-2 is entered as "2d". The real time spent was
7 hours. YouTrack counts one day as 8 hours, so it displays and reports as 16 hours.
The correct figure for DAT-2 is 7 hours.

ISSUES IN ITERATION 1
- The sqlite-jdbc and slf4j-api jars were ignored by .gitignore (*.jar), so
  teammates would not have had them after cloning. We committed the two jars
  to lib/ and documented the IntelliJ setup in the README.
- After merging a pull request, deleting Alex_branch while it was checked out left
  the local repo pointing at a branch with no commits, so every file showed as
  staged. We fixed it by switching to main and pulling.
- The MonsterFactory (DAT-13 in iteration 2) depends on the Ogre, Gremlin and
  Skeleton classes, which were still in progress, so it was deferred to
  iteration 2.
- Our UML shows a MonsterStats-based Monster constructor, but the Monster class
  takes the raw statistic values. The team still needs to settle on one design.
- The code uses "package src.model;" because of the repo layout. This works,
  but we may rename the packages later.

john
nathan
liiban
Swing refresher (2h)
  Reviewed Swing basics needed for the GUI: JFrame, JMenuBar, JMenu,
  JMenuItem, JOptionPane and action listeners. Built a practice window.
 
Main game window (3h)
  Created GameView (src.view), which extends JFrame: titled "Dungeon
  Adventure", 1100 x 750 (SRS 2.4), centered, and closes the program on exit.
  Includes setting up IntelliJ, the team GitHub repo and fixing the JUnit 5
  build error.
 
File and Help menus (3h)
  Added the menu bar (SRS 3.11, FR-MNU-1). File: New Game, Save Game,
  Load Game, Exit. Help: About, Game Play Instructions. About, Instructions
  and Exit work; New, Save and Load are placeholders until the model is ready.
 
 
ISSUES IN ITERATION 1
1. JUnit 5 was not on the classpath after cloning the repo, so the project
   failed to build ("package org.junit.jupiter.api does not exist"). Fixed by
   changing the JUnit5 dependency scope from Test to Compile.
