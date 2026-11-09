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

## Alex Douk

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

## John Nguyen

WORK COMPLETED
- DAT-1 Set up DungeonCharacter (8 hours actual): DungeonCharacter, the abstract parent of Hero and Monster, with private fields, a protected constructor that validates every value, takeDamage, a clamped setHitPoints, and attack(foe, rng), which takes the shared Random so tests can use a fixed seed (SRS 2.5). Serializable for save and load. Also Hero (chance to block, healing and vision potions, an abstract specialSkill, and names trimmed to 1 to 20 characters per FR-NEW-2) and Warrior, Priestess and Thief with the FR-HERO-1 statistics and the Crushing Blow, Heal and Surprise Attack skills. Full Javadoc on every class.
- DAT-13 Create Monster subclasses (2 hours): Monster, which adds a chance to heal and tryHeal (returns the hit points restored and never heals a fainted monster, FR-COM-6), and Ogre, Gremlin and Skeleton, which take a name and a MonsterStats object so no statistics are hard-coded (FR-MON-2).
- Total: 10 hours.

NOTE ON LOGGED TIME

DAT-1 was completed in the span of 3-4 days, but due to how YouTrack works, assigning multiple days on one task will result in an inaccurate time log. As a result, a time estimate of 8 hours were logged on a single day.

ISSUES IN ITERATION 1

- Hero is missing its list of pillars found (FR-HERO-6, FR-MOV-4) because the Pillar class is not written yet. A TODO is in the Hero Javadoc, and the list, addPillar, getPillars and the toString output will be added when Pillar exists.
- The special skills and tryHeal are not called from anywhere yet. GameModel (iteration 2) has to apply blocking, the extra-attacks rule and the monster's chance to heal around these methods (FR-COM-2 to FR-COM-6).
- The team has decided not to give individual names to monsters, so a monster's name is its type (Ogre, Gremlin, Skeleton) as stored in the database. Monster now has a constructor that takes a MonsterStats object, which resolves the mismatch with the UML noted in Alex's section.
- The code uses "package src.model;" to match the repo layout, the same as MonsterStats.

## Nathan Chiu

WORK COMPLETED
- DAT-3 Set up and implemented code to the Room class 
- DAT-15 Set up and implemented code to the RoomItem class

NOTE ON LOGGED TIME
The YouTrack time for DAT-3 is entered as "2d". The real time spent was
9 hours. YouTrack counts one day as 8 hours, so it displays and reports as 16 hours.
The correct figure for DAT-3 is 9 hours.

ISSUES IN ITERATION 1
- Pillar, Exit and Entrance are all "items" to be found in the room. Currently no code
  has been implemented in these classes so when ran and those items are shown in the
  room, nothing happens. 


## Liiban Isse

- Swing refresher (2h)
  Reviewed Swing basics needed for the GUI: JFrame, JMenuBar, JMenu,
  JMenuItem, JOptionPane and action listeners. Built a practice window.
 
- Main game window (3h)
  Created GameView (src.view), which extends JFrame: titled "Dungeon
  Adventure", 1100 x 750 (SRS 2.4), centered, and closes the program on exit.
  Includes setting up IntelliJ, the team GitHub repo and fixing the JUnit 5
  build error.
 
- File and Help menus (3h)
  Added the menu bar (SRS 3.11, FR-MNU-1). File: New Game, Save Game,
  Load Game, Exit. Help: About, Game Play Instructions. About, Instructions
  and Exit work; New, Save and Load are placeholders until the model is ready.
 
 
ISSUES IN ITERATION 1
1. JUnit 5 was not on the classpath after cloning the repo, so the project
   failed to build ("package org.junit.jupiter.api does not exist"). Fixed by
   changing the JUnit5 dependency scope from Test to Compile.


# Group 7 Iteration 2 (Nov 2 - Nov 8, 2026)

## Alex Douk

WORK COMPLETED
- DAT-20 Create MonsterFactory (4 hours): MonsterFactory builds Ogre, Gremlin and
  Skeleton objects from the stats stored in the SQLite database (SRS FR-MON-3).
  `create(type, guardian)` returns a new, separate monster each call and rejects
  blank, null and unsupported types. `createRandom(rng, guardian)` picks a type
  with the shared Random so a fixed seed gives repeatable results. Guardian
  monsters get +25% hit points and damage; attack speed, chance to hit and the
  heal stats are unchanged. JUnit tests in MonsterFactoryTest.
- DAT-<ticket> Create Dungeon and DungeonBuilder (<hours> hours): Dungeon is a
  grid of Rooms with an entrance, an exit and the hero's position. It has
  moveHero, an isTraversable check (breadth-first search from the entrance to
  the exit and all four pillars), a toString map built from the Room pictures
  (FR-GEN-7), and Serializable support for save and load. DungeonBuilder
  (Builder pattern) carves a random maze with a depth-first search, opens a few
  extra doors for loops, places the entrance, exit and four pillars in separate
  rooms, puts guardian monsters in the pillar and exit rooms, gives other rooms
  a 15% chance of a monster, and regenerates until the dungeon is traversable.
  A seeded Random gives the same dungeon every time. JUnit tests in
  DungeonBuilderTest.
- Total: <hours> hours.

ISSUES IN ITERATION 2
- Healing potions, vision potions and pits (FR-GEN-4) are not placed yet. They
  will be added to DungeonBuilder as a follow-up ticket now that the potion and
  pit item classes are being written.
- FR-GEN-1 asks for at least a 4 x 4 dungeon, but DungeonBuilder.setSize allows
  any size with 6 or more rooms (the entrance, exit and four pillars). The
  default is 5 x 5. The team needs to decide whether to enforce 4 x 4.
- The dungeon is always connected, so it always has dead ends, but the random
  extra doors could in theory remove them all. DungeonBuilder does not check
  for a dead end before returning a dungeon yet.
- The MonsterFactory ticket was planned as DAT-13 in the Iteration 1 notes, but
  it was created as DAT-20 in YouTrack (DAT-13 became the Monster subclasses).
- The guardian rule (+25% hit points and damage) is our own choice, because the
  SRS says the exact rule is still to be decided (FR-GEN-5).

## John

WORK COMPLETED
- DAT-22 Make JUnit tests for the DungeonCharacter classes (5 hours): CharacterTest, which tests 
Hero, Warrior, Priestess, Thief, Monster and Ogre. It covers name checks, hit points and damage, 
potions, the pillars list, the three special skills (Crushing Blow, Heal, Surprise Attack) and 
monster healing. A fixed-seed Random and a test monster that always hits and heals for exactly 
10 keep the results the same on every run (SRS 2.5).
- DAT-19 Implement Hero and Pillar integration (3 hours): Hero now keeps the Pillars of 
OO it has found, with addPillar (ignores duplicates), getPillars (read-only), hasAllPillars, and the 
pillars in toString (FR-HERO-6, FR-MOV-4).
- DAT-26 Implement Battle class and logic for Hero and Monster (3 hours): Battle plays a 
fight one round at a time with playRound(action), where the action is Attack, Special Skill 
or Healing Potion (FR-COM-2). The hero makes (hero speed ÷ monster speed) attacks per round, rounded down, 
with a minimum of 1, and a skill or potion replaces the normal attacks (FR-COM-3). The monster then 
attacks once. A fainted monster never attacks, and isOver and getWinner report the result (FR-COM-1). 
- Total: 11 hours.

ISSUES IN ITERATION 2
- Blocking (FR-COM-5), monster healing (FR-COM-6) and the full battle log (FR-COM-7) are not added to Battle yet.
  A TODO is in the Battle Javadoc, and they are planned for the next iteration.
- Battle is not connected to the GUI or to a game model yet. The battle panel and GameModel still need to call playRound.
- The SRS says the monster attacks once per round and that the exact rule is still to be decided (FR-COM-3, ISS-7).
  Battle follows the current wording, but it is subject to change after some group discussion later. Choosing a
  healing potion when the hero has none logs a message and does not use up the round, which is our own choice because the SRS does not say.
- A separate Battle class is our own design choice, because the SRS does not name one. It is not in the UML yet, so we will likely update that for next time.

## Nathan
- DAT-16 Set up and implemented code to the Exit class (1 Hour)
- DAT-17 Set up and implemented code to the Entrance class (1 Hour)
- DAT-18 Set up and implemented code to the Pillar class (4 Hour)
- DAT-25 Set up and implemented code to the Potion subclasses (2 Hour)
    Added more classes to represent the different types of potions within the game,
    as well as exit, entrance, and pillar which are other items to be found in rooms
- ISSUES: Some of these RoomItems have still yet to be used in the other classes for it to show in the game.

## Liiban
Split the window into areas (2h)
  Used BorderLayout in GameView: the dungeon map area in the center (grows
  with the window) and a 400-pixel side column on the right for the hero,
  room and battle panels (SRS 3.13). Added Javadoc and named constants in
  the team's style.
 
Hero panel (2h)
  Created HeroPanel (src.view), shown at the top of the side column. It shows
  the hero's name and class, healing and vision potion counts, and the
  pillars found (SRS FR-MOV-4). It reads a real Warrior from John's model
  classes through getters (getName, getHealingPotions, getVisionPotions,
  getPillars).
 
HP bar (2h)
  Replaced the hero's HP text with a JProgressBar that fills according to
  getHitPoints() out of getMaxHitPoints() and shows "HP current / max".
  Tested with takeDamage(50): the bar dropped to HP 75 / 125.
 
Room panel - part 1 (3h)
  Created RoomPanel (src.view), shown below the hero panel. North, South,
  East and West buttons are arranged like a compass, and only directions with
  a door are enabled (SRS FR-MOV-1), using Nathan's Room.hasDoor(). 
 
 
ISSUES
------
1. Pushed work goes to liiban_branch, not main, so it did not appear on main
   until a pull request was merged. I now open and merge a pull request after
   each finished story.
 
2. HeroPanel's call to getPillars() would not compile until I pulled and
   merged main, because John's Hero update was not yet on my branch. I now
   pull and merge main into my branch before starting work. Issue fixed.
 
4. The room panel's buttons stretch to fill the side column because the panel
   sits in BorderLayout's CENTER. This will be fixed when the potion buttons
   and battle panel are added, to match the planned layout.
