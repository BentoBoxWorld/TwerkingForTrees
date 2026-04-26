package world.bentobox.twerk.listeners;

import org.junit.jupiter.api.Test;

/**
 * Smoke tests for {@link TreeGrowListener}.
 * Full integration testing requires a Bukkit/BentoBox server environment.
 */
class TreeGrowListenerTest {

    @Test
    void testListenerClassExists() {
        // Verify the listener class can be loaded
        Class<TreeGrowListener> clazz = TreeGrowListener.class;
        assert (clazz != null);
        assert (clazz.getName().equals("world.bentobox.twerk.listeners.TreeGrowListener"));
    }

    @Test
    void testListenerHasRequiredEventHandlers() {
        // Verify all event handler methods exist
        assert (TreeGrowListener.class.getDeclaredMethods().length > 0);

        boolean hasOnTwerk = java.util.Arrays.stream(TreeGrowListener.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("onTwerk"));
        assert (hasOnTwerk) : "Missing onTwerk method";

        boolean hasOnSprint = java.util.Arrays.stream(TreeGrowListener.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("onSprint"));
        assert (hasOnSprint) : "Missing onSprint method";

        boolean hasOnTreeBreak = java.util.Arrays.stream(TreeGrowListener.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("onTreeBreak"));
        assert (hasOnTreeBreak) : "Missing onTreeBreak method";

        boolean hasOnTreeGrow = java.util.Arrays.stream(TreeGrowListener.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("onTreeGrow"));
        assert (hasOnTreeGrow) : "Missing onTreeGrow method";
    }
}
