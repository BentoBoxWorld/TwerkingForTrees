package world.bentobox.twerk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.bukkit.Sound;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import world.bentobox.bentobox.api.addons.AddonDescription;
import world.bentobox.bentobox.database.AbstractDatabaseHandler;
import world.bentobox.bentobox.database.DatabaseSetup;
import world.bentobox.bentobox.managers.AddonsManager;
import world.bentobox.bentobox.managers.CommandsManager;

/**
 * Tests for {@link TwerkingForTrees}.
 */
class TwerkingForTreesTest extends CommonTestSetup {

    private static final String CONFIG_YML =
            """
                    minimum-twerks: 4
                    hold-for-twerk: false
                    sprint-to-grow: false
                    range: 5
                    sounds:
                      enabled: true
                      twerk:
                        sound: block.note_block.bass
                        volume: 1.0
                        pitch: 2.0
                      growing-small-tree:
                        sound: block.bubble_column.upwards_ambient
                        volume: 1.0
                        pitch: 1.0
                      growing-big-tree:
                        sound: block.bubble_column.upwards_ambient
                        volume: 1.0
                        pitch: 1.0
                    effects:
                      enabled: true
                      twerk: MOBSPAWNER_FLAMES
                    """;


    private TwerkingForTrees addon;
    private MockedStatic<DatabaseSetup> mockDb;

    @SuppressWarnings("unchecked")
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        // Database mock
        AbstractDatabaseHandler<Object> h = mock(AbstractDatabaseHandler.class);
        mockDb = Mockito.mockStatic(DatabaseSetup.class);
        DatabaseSetup dbSetup = mock(DatabaseSetup.class);
        mockDb.when(DatabaseSetup::getDatabase).thenReturn(dbSetup);
        when(dbSetup.getHandler(any())).thenReturn(h);
        when(h.saveObject(any())).thenReturn(CompletableFuture.completedFuture(true));

        // CommandsManager
        CommandsManager cm = mock(CommandsManager.class);
        when(plugin.getCommandsManager()).thenReturn(cm);

        // AddonsManager
        AddonsManager am = mock(AddonsManager.class);
        when(plugin.getAddonsManager()).thenReturn(am);

        // FlagsManager
        when(plugin.getFlagsManager()).thenReturn(fm);
        when(fm.getFlags()).thenReturn(Collections.emptyList());

        // Create addon with a JAR containing config.yml
        addon = new TwerkingForTrees();
        File jFile = new File("addon.jar");
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jFile))) {
            addJarEntry(jos, "config.yml", CONFIG_YML);
        }
        File dataFolder = new File("addons/TwerkingForTrees");
        addon.setDataFolder(dataFolder);
        addon.setFile(jFile);
        AddonDescription desc = new AddonDescription.Builder("bentobox", "TwerkingForTrees", "1.0.0")
                .description("test").authors("tastybento").build();
        addon.setDescription(desc);
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        if (mockDb != null) {
            mockDb.closeOnDemand();
        }
        super.tearDown();
        new File("addon.jar").delete();
        deleteAll(new File("addons"));
    }

    private static void addJarEntry(JarOutputStream jos, String name, String content) throws Exception {
        JarEntry entry = new JarEntry(name);
        jos.putNextEntry(entry);
        jos.write(content.getBytes(StandardCharsets.UTF_8));
        jos.closeEntry();
    }

    @Test
    void testGetSettingsNullBeforeLoad() {
        assertNull(addon.getSettings());
    }

    @Test
    void testOnLoad() {
        addon.onLoad();
        assertNotNull(addon.getSettings());
    }

    @Test
    void testOnLoadSettingsDefaults() {
        addon.onLoad();
        Settings s = addon.getSettings();
        assertNotNull(s);
        assertEquals(4, s.getMinimumTwerks());
        assertEquals(5, s.getRange());
        assertEquals(false, s.isHoldForTwerk());
        assertEquals(false, s.isSprintToGrow());
        assertEquals(true, s.isSoundsEnabled());
        assertEquals(Sound.BLOCK_NOTE_BLOCK_BASS, s.getSoundsTwerkSound());
        assertEquals(1.0, s.getSoundsTwerkVolume());
        assertEquals(2.0, s.getSoundsTwerkPitch());
        assertEquals(true, s.isEffectsEnabled());
    }

    @Test
    void testOnEnable() {
        addon.onLoad();
        addon.onEnable();
        assertNotNull(addon);
    }

    @Test
    void testOnDisable() {
        addon.onDisable();
        assertNotNull(addon);
    }

    @Test
    void testOnReload() {
        addon.onLoad();
        addon.onReload();
        assertNotNull(addon.getSettings());
    }

    @Test
    void testOnReloadPreservesSettings() {
        addon.onLoad();
        addon.onReload();
        assertEquals(4, addon.getSettings().getMinimumTwerks());
    }

    @Test
    void testOnLoadGrowingSoundDefaults() {
        addon.onLoad();
        Settings s = addon.getSettings();
        assertEquals(Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, s.getSoundsGrowingSmallTreeSound());
        assertEquals(1.0, s.getSoundsGrowingSmallTreeVolume());
        assertEquals(1.0, s.getSoundsGrowingSmallTreePitch());
        assertEquals(Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, s.getSoundsGrowingBigTreeSound());
        assertEquals(1.0, s.getSoundsGrowingBigTreeVolume());
        assertEquals(1.0, s.getSoundsGrowingBigTreePitch());
    }
}
