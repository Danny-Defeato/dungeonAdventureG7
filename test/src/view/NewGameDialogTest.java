package src.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import src.model.HeroFactory;

/**
 * Tests for {@link NewGameDialog}'s name rules (FR-NEW-2) and hero cards
 * (FR-NEW-3). The dialog window itself is not opened, so these tests also
 * run without a screen.
 *
 * @author Group 7 (Alex Douk)
 */
class NewGameDialogTest {

    @Test
    void normalNameIsAccepted() {
        assertNull(NewGameDialog.checkName("Gristle"));
    }

    @Test
    void oneAndTwentyCharacterNamesAreAccepted() {
        assertNull(NewGameDialog.checkName("A"));
        assertNull(NewGameDialog.checkName("A".repeat(NewGameDialog.MAX_NAME_LENGTH)));
    }

    @Test
    void spacesAroundTheNameDoNotCount() {
        assertNull(NewGameDialog.checkName("   "
                + "B".repeat(NewGameDialog.MAX_NAME_LENGTH) + "   "));
    }

    @Test
    void emptyBlankAndNullNamesAreRejected() {
        assertNotNull(NewGameDialog.checkName(""));
        assertNotNull(NewGameDialog.checkName("    "));
        assertNotNull(NewGameDialog.checkName(null));
    }

    @Test
    void nameLongerThanTwentyIsRejected() {
        assertNotNull(NewGameDialog.checkName(
                "C".repeat(NewGameDialog.MAX_NAME_LENGTH + 1)));
    }

    @Test
    void cardsMatchTheHeroFactoryTypes() {
        assertEquals(HeroFactory.getSupportedTypes(), NewGameDialog.getCardTypes());
    }

    @Test
    void everyAcceptedNameCreatesAHero() {
        for (final String type : NewGameDialog.getCardTypes()) {
            assertEquals("  Zed  ".trim(),
                    new HeroFactory().create(type, "  Zed  ").getName());
        }
    }
}
