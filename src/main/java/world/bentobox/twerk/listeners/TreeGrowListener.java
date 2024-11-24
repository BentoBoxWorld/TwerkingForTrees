package world.bentobox.twerk.listeners;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
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
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.eclipse.jdt.annotation.NonNull;

import com.google.common.base.Enums;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;
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
        conv.put(Material.AZALEA, TreeType.AZALEA);
        conv.put(Material.FLOWERING_AZALEA, TreeType.AZALEA);
        conv.put(Material.MANGROVE_PROPAGULE, TreeType.MANGROVE);
        conv.put(Material.CHERRY_SAPLING, TreeType.CHERRY);
        conv.put(Material.DARK_OAK_SAPLING, TreeType.DARK_OAK);
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

    private static final Random RAND = new Random();

    private TwerkingForTrees addon;
    private Map<Island, Integer> twerkCount;
    private Set<Island> isTwerking;
    private Map<Location, Island> plantedTrees;
    private Set<Player> twerkers = new HashSet<>();
    private Set<Player> sprinters = new HashSet<>();

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
            // Clear plantedTrees if no one on island is twerking
            plantedTrees.values().removeIf(i -> !isTwerking.contains(i));
        }
        , 0L, 40L);
        // Every 10 seconds
        Bukkit.getScheduler().runTaskTimer(addon.getPlugin(), () ->
        plantedTrees
        .entrySet()
        .stream()
        .filter(e -> isTwerking.contains(e.getValue()))
        .map(Map.Entry::getKey)
        .forEach(b -> Util.getChunkAtAsync(b).thenRun(() -> growTree(b.getBlock())))
        , 10L, 400L);
        // Simulate twerking
        if (addon.getSettings().isHoldForTwerk()) {
            Bukkit.getScheduler().runTaskTimer(addon.getPlugin(), () -> twerkers.forEach(this::twerk), 0L, 5L);
        }
        // Sprinting
        if (addon.getSettings().isSprintToGrow()) {
            Bukkit.getScheduler().runTaskTimer(addon.getPlugin(), () -> sprinters.forEach(this::twerk), 0L, 5L);
        }
    }

    protected void growTree(Block b) {
        Material t = b.getType();
        if (!Tag.SAPLINGS.isTagged(t)) {
            return;
        }
        // Try to grow big tree if possible
        if (SAPLING_TO_BIG_TREE_TYPE.containsKey(t) && bigTreeSaplings(b)) {
            return;
        }
        if (SAPLING_TO_TREE_TYPE.containsKey(t)) {
            TreeType type = SAPLING_TO_TREE_TYPE.getOrDefault(b.getType(), TreeType.TREE);
            BentoBox.getInstance().logDebug("Setting " + b + " mat " + t + " to air");
            b.setType(Material.AIR);
            if (b.getWorld().generateTree(b.getLocation(), RAND, type, (Predicate<BlockState>) this::checkPlace)) {
                if (addon.getSettings().isEffectsEnabled()) {
                    showSparkles(b);
                }
                if (addon.getSettings().isSoundsEnabled()) {
                    b.getWorld().playSound(b.getLocation(), addon.getSettings().getSoundsGrowingSmallTreeSound(),
                            (float) addon.getSettings().getSoundsGrowingSmallTreeVolume(),
                            (float) addon.getSettings().getSoundsGrowingSmallTreePitch());
                }
            } else {
                // Tree generation failed, so reset block
                b.setType(t);
            }
        }
    }

    private Boolean checkPlace(BlockState bs) {
        System.out.println("Not Dirt " + (bs.getType() != Material.DIRT));
        System.out.println("Outside range flag set? " + Flags.TREES_GROWING_OUTSIDE_RANGE.isSetForWorld(bs.getWorld()));
        System.out.println("Inside island? " + addon.getIslands().getProtectedIslandAt(bs.getLocation()).isPresent());
        System.out.println("Overall = " + (bs.getType() != Material.DIRT && (Flags.TREES_GROWING_OUTSIDE_RANGE.isSetForWorld(bs.getWorld())
                || addon.getIslands().getProtectedIslandAt(bs.getLocation()).isPresent())));
        return bs.getType() != Material.DIRT && (Flags.TREES_GROWING_OUTSIDE_RANGE.isSetForWorld(bs.getWorld())
                || addon.getIslands().getProtectedIslandAt(bs.getLocation()).isPresent());
    }

    protected boolean bigTreeSaplings(Block b) {
        Material treeType = b.getType();
        TreeType type = SAPLING_TO_BIG_TREE_TYPE.get(treeType);

        for (List<BlockFace> quad : QUADS) {
            if (isQuadOfSameType(b, quad, treeType)) {
                clearSaplings(b, quad);
                Location treeLocation = b.getRelative(quad.get(0)).getLocation();

                if (generateBigTree(b, treeLocation, type)) {
                    playBigTreeEffectsAndSounds(b);
                    return true;
                } else {
                    resetSaplings(b, quad, treeType);
                }
            }
        }
        return false;
    }

    private boolean isQuadOfSameType(Block b, List<BlockFace> quad, Material treeType) {
        return quad.stream().map(b::getRelative).allMatch(c -> c.getType().equals(treeType));
    }

    private void clearSaplings(Block b, List<BlockFace> quad) {
        quad.stream().map(b::getRelative).forEach(c -> c.setType(Material.AIR));
    }

    private boolean generateBigTree(Block b, Location location, TreeType type) {
        return b.getWorld().generateTree(location, RAND, type,
                bs -> Flags.TREES_GROWING_OUTSIDE_RANGE.isSetForWorld(bs.getWorld())
                        || addon.getIslands().getProtectedIslandAt(bs.getLocation()).isPresent());
    }

    private void playBigTreeEffectsAndSounds(Block b) {
        if (addon.getSettings().isEffectsEnabled()) {
            showSparkles(b);
        }
        if (addon.getSettings().isSoundsEnabled()) {
            b.getWorld().playSound(b.getLocation(), addon.getSettings().getSoundsGrowingBigTreeSound(),
                    (float) addon.getSettings().getSoundsGrowingBigTreeVolume(),
                    (float) addon.getSettings().getSoundsGrowingBigTreePitch());
        }
    }

    private void resetSaplings(Block b, List<BlockFace> quad, Material treeType) {
        quad.stream().map(b::getRelative).forEach(c -> c.setType(treeType));
    }

    protected void showSparkles(Block b) {
        AROUND.stream().map(b::getRelative).map(Block::getLocation).forEach(x -> x.getWorld().playEffect(x, addon.getSettings().getEffectsTwerk(), 0));
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
        if (check(e.getPlayer())) {
            return;
        }
        if (addon.getSettings().isHoldForTwerk()) {
            Player player = e.getPlayer();
            if (!twerkers.add(player)) {
                twerkers.remove(player);
            }
            return;
        }
        twerk(e.getPlayer());
    }

    private boolean check(Player player) {
        return !player.getWorld().getEnvironment().equals(Environment.NORMAL)
                || !addon.getPlugin().getIWM().inWorld(Util.getWorld(player.getWorld())) || player.isFlying()
                || !player.hasPermission(
                        addon.getPlugin().getIWM().getPermissionPrefix(player.getWorld()) + "twerkingfortrees");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSprint(PlayerToggleSprintEvent e) {
        if (check(e.getPlayer())) {
            return;
        }
        if (addon.getSettings().isSprintToGrow()) {
            Player player = e.getPlayer();
            if (!sprinters.add(player)) {
                sprinters.remove(player);
            }
            return;
        }
        twerk(e.getPlayer());
    }

    private void twerk(Player player) {
        // Get the island
        addon.getIslands().getIslandAt(player.getLocation()).ifPresent(i -> {
            // Check if there are any planted saplings around player
            if (!twerkCount.containsKey(i) || twerkCount.get(i) == 0) {
                // New twerking effort
                getNearbySaplings(player, i);
            }
            if (!plantedTrees.values().contains(i)) {
                // None, so return
                return;
            }

            twerkCount.putIfAbsent(i, 0);
            int count = twerkCount.get(i) + 1;
            twerkCount.put(i, count);
            if (count >= addon.getSettings().getMinimumTwerks()) {
                player.playSound(player.getLocation(), addon.getSettings().getSoundsTwerkSound(),
                        (float)addon.getSettings().getSoundsTwerkVolume(), (float)addon.getSettings().getSoundsTwerkPitch());
                Particle p = Enums.getIfPresent(Particle.class, "SPELL").orNull();
                if (p == null) {
                    p = Enums.getIfPresent(Particle.class, "POOF").orNull();
                }
                if (p != null) {
                    player.spawnParticle(p, player.getLocation(), 20, 3D, 0D, 3D);
                }
            }
        });

    }

    private void getNearbySaplings(Player player, Island i) {
        plantedTrees.values().removeIf(i::equals);
        int range = addon.getSettings().getRange();
        for (int x = player.getLocation().getBlockX() - range ; x <= player.getLocation().getBlockX() + range; x++) {
            for (int y = player.getLocation().getBlockY() - range ; y <= player.getLocation().getBlockY() + range; y++) {
                for (int z = player.getLocation().getBlockZ() - range ; z <= player.getLocation().getBlockZ() + range; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    if (Tag.SAPLINGS.isTagged(block.getType())) {
                        plantedTrees.put(block.getLocation(), i);
                    }
                }
            }
        }

    }
}