package world.bentobox.twerk.events;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import world.bentobox.twerk.TwerkingForTrees;

public class TreeGrowListener implements Listener {

    // The first entry in the list of the quads is where the big tree should be planted - always most positive x and z.
    private static final List<BlockFace> QUAD1 = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.NORTH_EAST, BlockFace.SELF);
    private static final List<BlockFace> QUAD2 = Arrays.asList(BlockFace.NORTH_WEST, BlockFace.SELF, BlockFace.WEST, BlockFace.NORTH);
    private static final List<BlockFace> QUAD3 = Arrays.asList(BlockFace.WEST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.SELF);
    private static final List<BlockFace> QUAD4 = Arrays.asList(BlockFace.SELF, BlockFace.SOUTH_EAST, BlockFace.EAST, BlockFace.SOUTH);
    private static final List<List<BlockFace>> QUADS = Arrays.asList(QUAD1, QUAD2, QUAD3, QUAD4);

    private static final List<BlockFace> AROUND  = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.NORTH_EAST,
            BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST);

    private static final int TWERK_MIN = 4;
    /**
     * Converts between sapling and tree type. Why doesn't this API exist already I wonder?
     */
    private static final Map<Material, TreeType> SAPLING_TO_TREE_TYPE;
    static {
        Map<Material, TreeType> conv = new EnumMap<>(Material.class);
        conv.put(Material.ACACIA_SAPLING, TreeType.ACACIA);
        conv.put(Material.BIRCH_SAPLING, TreeType.BIRCH);
        conv.put(Material.JUNGLE_SAPLING, TreeType.SMALL_JUNGLE);
        conv.put(Material.OAK_SAPLING, TreeType.TREE);
        conv.put(Material.SPRUCE_SAPLING, TreeType.REDWOOD);
        SAPLING_TO_TREE_TYPE = Collections.unmodifiableMap(conv);
    }
    private static final Map<Material, TreeType> SAPLING_TO_BIG_TREE_TYPE;
    static {
        Map<Material, TreeType> conv = new EnumMap<>(Material.class);
        conv.put(Material.DARK_OAK_SAPLING, TreeType.DARK_OAK);
        conv.put(Material.SPRUCE_SAPLING, TreeType.MEGA_REDWOOD);
        conv.put(Material.JUNGLE_SAPLING, TreeType.JUNGLE);
        SAPLING_TO_BIG_TREE_TYPE = Collections.unmodifiableMap(conv);
    }

    private TwerkingForTrees addon;
    private Map<Island, Integer> twerkCount;
    private Set<Island> isTwerking;
    private Map<Block, Island> plantedTrees;

    public TreeGrowListener(@NonNull TwerkingForTrees addon) {
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
        plantedTrees
        .entrySet()
        .stream()
        .filter(e -> isTwerking.contains(e.getValue()))
        .map(Map.Entry::getKey)
        .forEach(this::growTree)
        , 10L, 400L);
    }

    private void growTree(Block b) {
        if (!Tag.SAPLINGS.isTagged(b.getType())) {
            return;
        }
        // Try to grow big tree if possible
        if (SAPLING_TO_BIG_TREE_TYPE.containsKey(b.getType()) && bigTreeSaplings(b)) {
            return;
        } else if (SAPLING_TO_TREE_TYPE.containsKey(b.getType())) {
            TreeType type = SAPLING_TO_TREE_TYPE.getOrDefault(b.getType(), TreeType.TREE);
            b.setType(Material.AIR);
            b.getWorld().generateTree(b.getLocation(), type);
            showSparkles(b);
            addon.getPlugin().logDebug("Growing 1x1 tree " + type);
            b.getWorld().playSound(b.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, 1F, 1F);
        }
    }

    private boolean bigTreeSaplings(Block b) {
        TreeType type = SAPLING_TO_BIG_TREE_TYPE.get(b.getType());
        for (List<BlockFace> q : QUADS) {
            if (q.stream().map(b::getRelative).allMatch(c -> c.getType().equals(b.getType()))) {
                // All the same sapling type found in this quad
                q.stream().map(b::getRelative).forEach(c -> c.setType(Material.AIR));
                addon.getPlugin().logDebug("Growing big tree");
                // Get the tree planting location
                Location l = b.getRelative(q.get(0)).getLocation();
                b.getWorld().generateTree(l, type);
                showSparkles(b);
                b.getWorld().playSound(b.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, 1F, 1F);
                return true;
            }
        }
        return false;
    }

    private void showSparkles(Block b) {
        AROUND.stream().map(b::getRelative).map(Block::getLocation).forEach(x -> x.getWorld().playEffect(x, Effect.MOBSPAWNER_FLAMES, 0));
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
        if (!addon.getPlugin().getIWM().inWorld(Util.getWorld(e.getPlayer().getWorld()))
                || e.getPlayer().isFlying()) {
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