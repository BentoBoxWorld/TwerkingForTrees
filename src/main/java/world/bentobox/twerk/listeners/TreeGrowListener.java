package world.bentobox.twerk.listeners;

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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.TreeType;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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

    protected void runChecker() {
        // Every two seconds
        Bukkit.getScheduler().runTaskTimer(addon.getPlugin(), () -> {
            isTwerking = twerkCount.entrySet().stream().filter(e -> e.getValue() > addon.getSettings().getMinimumTwerks()).map(Map.Entry::getKey).collect(Collectors.toSet());
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
        .forEach(b -> Util.getChunkAtAsync(b.getLocation()).thenRun(() -> growTree(b)))
        , 10L, 400L);
    }

    protected void growTree(Block b) {
        Material t = b.getType();
        if (!Tag.SAPLINGS.isTagged(t)) {
            return;
        }
        // Try to grow big tree if possible
        if (SAPLING_TO_BIG_TREE_TYPE.containsKey(t) && bigTreeSaplings(b)) {
            return;
        } else if (SAPLING_TO_TREE_TYPE.containsKey(t)) {
            TreeType type = SAPLING_TO_TREE_TYPE.getOrDefault(b.getType(), TreeType.TREE);
            b.setType(Material.AIR);
            if (b.getWorld().generateTree(b.getLocation(), type)) {
                if (addon.getSettings().isEffectsEnabled()) {
                    showSparkles(b);
                }
                if (addon.getSettings().isSoundsEnabled()) {
                    b.getWorld().playSound(b.getLocation(), addon.getSettings().getSoundsGrowingSmallTreeSound(),
                            (float)addon.getSettings().getSoundsGrowingSmallTreeVolume(), (float)addon.getSettings().getSoundsGrowingSmallTreePitch());
                }
            } else {
                // Tree generation failed, so reset block
                b.setType(t);
            }
        }
    }

    protected boolean bigTreeSaplings(Block b) {
        Material treeType = b.getType();
        TreeType type = SAPLING_TO_BIG_TREE_TYPE.get(treeType);
        for (List<BlockFace> q : QUADS) {
            if (q.stream().map(b::getRelative).allMatch(c -> c.getType().equals(treeType))) {
                // All the same sapling type found in this quad
                q.stream().map(b::getRelative).forEach(c -> c.setType(Material.AIR));
                // Get the tree planting location
                Location l = b.getRelative(q.get(0)).getLocation();
                if (b.getWorld().generateTree(l, type)) {
                    if (addon.getSettings().isEffectsEnabled()) {
                        showSparkles(b);
                    }
                    if (addon.getSettings().isSoundsEnabled()) {
                        b.getWorld().playSound(b.getLocation(), addon.getSettings().getSoundsGrowingBigTreeSound(),
                                (float)addon.getSettings().getSoundsGrowingBigTreeVolume(), (float)addon.getSettings().getSoundsGrowingBigTreePitch());
                    }
                    return true;
                } else {
                    // Generation failed, reset saplings
                    q.stream().map(b::getRelative).forEach(c -> c.setType(treeType));
                }
            }
        }
        return false;
    }

    protected void showSparkles(Block b) {
        AROUND.stream().map(b::getRelative).map(Block::getLocation).forEach(x -> x.getWorld().playEffect(x, addon.getSettings().getEffectsTwerk(), 0));
    }

    /*
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTreePlant(BlockPlaceEvent e) {
        if (!e.getBlock().getWorld().getEnvironment().equals(Environment.NORMAL)
                || !addon.getPlugin().getIWM().inWorld(Util.getWorld(e.getBlock().getWorld()))
                || !Tag.SAPLINGS.isTagged(e.getBlock().getType())) {
            return;
        }
        // Add Sapling
        addon.getIslands().getIslandAt(e.getBlock().getLocation()).ifPresent(i -> plantedTrees.put(e.getBlock(), i));
    }
     */
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
        if (!e.getPlayer().getWorld().getEnvironment().equals(Environment.NORMAL)
                || !addon.getPlugin().getIWM().inWorld(Util.getWorld(e.getPlayer().getWorld()))
                || e.getPlayer().isFlying()) {
            return;
        }
        // Get the island
        addon.getIslands().getIslandAt(e.getPlayer().getLocation()).ifPresent(i -> {
            // Check if there are any planted saplings around player
            if (!twerkCount.containsKey(i) || twerkCount.get(i) == 0) {
                // New twerking effort
                getNearbySaplings(e.getPlayer(), i);
            }
            if (!plantedTrees.values().contains(i)) {
                // None, so return
                return;
            }

            twerkCount.putIfAbsent(i, 0);
            int count = twerkCount.get(i) + 1;
            twerkCount.put(i, count);
            if (count == addon.getSettings().getMinimumTwerks()) {
                e.getPlayer().playSound(e.getPlayer().getLocation(), addon.getSettings().getSoundsTwerkSound(),
                        (float)addon.getSettings().getSoundsTwerkVolume(), (float)addon.getSettings().getSoundsTwerkPitch());
                e.getPlayer().spawnParticle(Particle.SPELL, e.getPlayer().getLocation(), 20, 3D, 0D, 3D);
            }
        });
    }

    private void getNearbySaplings(Player player, Island i) {
        int range = addon.getSettings().getRange();
        for (int x = player.getLocation().getBlockX() - range ; x <= player.getLocation().getBlockX() + range; x++) {
            for (int y = player.getLocation().getBlockY() - range ; y <= player.getLocation().getBlockY() + range; y++) {
                for (int z = player.getLocation().getBlockZ() - range ; z <= player.getLocation().getBlockZ() + range; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    if (Tag.SAPLINGS.isTagged(block.getType())) {
                        plantedTrees.put(block, i);
                    }
                }
            }
        }

    }
}
