package world.bentobox.twerk.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
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
        // Apply 100 bonemeals - this seems to be enough
        for (int i = 0; i < 100; i++) {
            b.applyBoneMeal(BlockFace.UP);
        }
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
        if (!e.getPlayer().getWorld().getEnvironment().equals(Environment.NORMAL)
                || !addon.getPlugin().getIWM().inWorld(Util.getWorld(e.getPlayer().getWorld()))
                || e.getPlayer().isFlying()
                || !e.getPlayer().hasPermission(addon.getPlugin().getIWM().getPermissionPrefix(e.getPlayer().getWorld()) + "twerkingfortrees")) {
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
                e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), addon.getSettings().getSoundsTwerkSound(),
                        (float)addon.getSettings().getSoundsTwerkVolume(), (float)addon.getSettings().getSoundsTwerkPitch());
                e.getPlayer().getWorld().spawnParticle(Particle.SPELL, e.getPlayer().getLocation(), 20, 3D, 0D, 3D);
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
