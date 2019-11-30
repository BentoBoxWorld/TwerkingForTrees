package world.bentobox.twerk.events;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.eclipse.jdt.annotation.NonNull;

import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;
import world.bentobox.twerk.ForTrees;

public class TreeGrowEvent implements Listener {

    /**
     * Converts between sapling and tree type. Why doesn't this API exist already I wonder?
     */
    private static final Map<Material, TreeType> SAPLING_TO_TREE_TYPE;
    private static final int TWERK_MIN = 4;
    static {
        Map<Material, TreeType> conv = new EnumMap<>(Material.class);
        conv.put(Material.ACACIA_SAPLING, TreeType.ACACIA);
        conv.put(Material.BIRCH_SAPLING, TreeType.BIRCH);
        conv.put(Material.DARK_OAK_SAPLING, TreeType.DARK_OAK);
        conv.put(Material.JUNGLE_SAPLING, TreeType.JUNGLE);
        conv.put(Material.OAK_SAPLING, TreeType.TREE);
        conv.put(Material.SPRUCE_SAPLING, TreeType.REDWOOD);
        SAPLING_TO_TREE_TYPE = Collections.unmodifiableMap(conv);
    }
    private ForTrees addon;
    private Map<Island, Integer> twerkCount;
    private Set<Island> isTwerking;
    private Map<Block, Island> plantedTrees;

    public TreeGrowEvent(@NonNull ForTrees addon) {
        this.addon = addon;
        twerkCount = new HashMap<>();
        isTwerking = new HashSet<>();
        plantedTrees = new HashMap<>();
        runChecker();
    }

    private void runChecker() {
        // Every two seconds
        Bukkit.getScheduler().runTaskTimer(addon.getPlugin(), () -> {
            isTwerking = twerkCount.entrySet().stream().filter(e -> e.getValue() > TWERK_MIN).map(Map.Entry::getKey).collect(Collectors.toSet());
            twerkCount.clear();
            plantedTrees.keySet().removeIf(k -> !Tag.SAPLINGS.isTagged(k.getType()));
        }
        , 0L, 40L);
        // Every 20 seconds
        Bukkit.getScheduler().runTaskTimer(addon.getPlugin(), () ->
        plantedTrees.entrySet().stream().filter(e -> isTwerking.contains(e.getValue()))
        .map(Map.Entry::getKey).forEach(b -> {
            if (Tag.SAPLINGS.isTagged(b.getType())) {
                TreeType type = SAPLING_TO_TREE_TYPE.getOrDefault(b.getType(), TreeType.TREE);
                b.setType(Material.AIR);
                b.getWorld().generateTree(b.getLocation(), type);
                b.getWorld().playEffect(b.getLocation(), Effect.VILLAGER_PLANT_GROW, 2);
                b.getWorld().playSound(b.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, 1F, 1F);
            } else {
                plantedTrees.remove(b);
            }
        }), 10L, 400L);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTreePlant(BlockPlaceEvent e) {
        if (!addon.getPlugin().getIWM().inWorld(Util.getWorld(e.getBlock().getWorld()))
                || !Tag.SAPLINGS.isTagged(e.getBlock().getType())) {
            return;
        }
        // Add Sapling
        addon.getIslands().getIslandAt(e.getBlock().getLocation()).ifPresent(i -> plantedTrees.put(e.getBlock(), i));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTreeBreak(BlockBreakEvent e) {
        plantedTrees.keySet().removeIf(e.getBlock()::equals);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTreeGrow(StructureGrowEvent e) {
        e.getBlocks().forEach(b -> plantedTrees.keySet().removeIf(b.getBlock()::equals));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTwerk(PlayerToggleSneakEvent e) {
        if (!addon.getPlugin().getIWM().inWorld(Util.getWorld(e.getPlayer().getWorld()))) {
            return;
        }
        // Get the island
        addon.getIslands().getIslandAt(e.getPlayer().getLocation()).ifPresent(i -> {
            twerkCount.putIfAbsent(i, 0);
            int count = twerkCount.get(i) + 1;
            twerkCount.put(i, count);
            if (count == TWERK_MIN) {
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 2F);
            }
        });
    }
}
