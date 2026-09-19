package net.dafealru.pinataspectralite.utils;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ColorUtilsTest {

    @Test
    @DisplayName("Should convert hex format &#RRGGBB to MiniMessage format")
    void testHexConversion() {
        String input = "&#EC4899Hello World";
        String mm = ColorUtils.normalizeToMiniMessage(input);
        assertEquals("<#EC4899>Hello World", mm);
    }

    @Test
    @DisplayName("Should convert legacy color codes &a, &e, &l to MiniMessage format")
    void testLegacyConversion() {
        String input = "&aGreen &lBold &eYellow";
        String mm = ColorUtils.normalizeToMiniMessage(input);
        assertEquals("<green>Green <bold>Bold <yellow>Yellow", mm);
    }

    @Test
    @DisplayName("Should preserve existing MiniMessage gradient tags")
    void testGradientPreservation() {
        String input = "<gradient:#EC4899:#FCD34D><bold>PIÑATA</bold></gradient>";
        String mm = ColorUtils.normalizeToMiniMessage(input);
        assertEquals("<gradient:#EC4899:#FCD34D><bold>PIÑATA</bold></gradient>", mm);
    }

    @Test
    @DisplayName("Should normalize typo like &#f or &#a into legacy code")
    void testBrokenTypoFix() {
        String input = "&#EC4899⚡ &#FCD34D&l¡FASE! › &#f¡Texto!";
        String mm = ColorUtils.normalizeToMiniMessage(input);
        assertTrue(mm.contains("<#EC4899>⚡ <#FCD34D><bold>¡FASE! › <white>¡Texto!"));
    }

    @Test
    @DisplayName("Should correctly parse Component without throwing exceptions")
    void testComponentParsing() {
        String input = "<gradient:#EC4899:#8B5CF6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>";
        Component comp = ColorUtils.colorizeComponent(input);
        assertNotNull(comp);
    }

    @Test
    @DisplayName("Should correctly strip color codes from raw strings")
    void testStripColor() {
        String input = "&#EC4899&lPiñata <yellow>Fiesta</yellow>";
        String stripped = ColorUtils.stripColor(input);
        assertEquals("Piñata Fiesta", stripped);
    }
}
