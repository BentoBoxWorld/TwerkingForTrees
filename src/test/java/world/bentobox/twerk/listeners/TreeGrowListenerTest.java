package world.bentobox.twerk.listeners;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.Test;

/**
 * Smoke tests for {@link TreeGrowListener}.
 * Full integration testing requires a Bukkit/BentoBox server environment.
 */
class TreeGrowListenerTest {

    @Test
    void testListenerImplementsBukkitListener() {
        assertTrue(Listener.class.isAssignableFrom(TreeGrowListener.class),
                "TreeGrowListener must implement org.bukkit.event.Listener");
    }

    @Test
    void testListenerHasRequiredEventHandlers() {
        Set<String> handlers = Stream.of(TreeGrowListener.class.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(EventHandler.class))
                .map(Method::getName)
                .collect(Collectors.toSet());
        assertTrue(handlers.contains("onTwerk"), "Missing onTwerk @EventHandler");
        assertTrue(handlers.contains("onSprint"), "Missing onSprint @EventHandler");
        assertTrue(handlers.contains("onTreeBreak"), "Missing onTreeBreak @EventHandler");
        assertTrue(handlers.contains("onTreeGrow"), "Missing onTreeGrow @EventHandler");
    }
}
