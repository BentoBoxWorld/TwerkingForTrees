package world.bentobox.twerk.listeners;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import world.bentobox.bentobox.lists.Flags;
import world.bentobox.twerk.TwerkingForTrees;

/**
 * Handles the block changing done by tree growing to make sure they don't go outside island boundaries
 * @author tastybento
 *
 */
public class BlockChangeHandler implements BlockChangeDelegate {

    private final TwerkingForTrees addon;
    private final World world;

    /**
     * @param addon - addon
     * @param world - world
     */
    public BlockChangeHandler(TwerkingForTrees addon, World world) {
        this.addon = addon;
        this.world = world;
    }

    /* (non-Javadoc)
     * @see org.bukkit.BlockChangeDelegate#setBlockData(int, int, int, org.bukkit.block.data.BlockData)
     */
    @Override
    public boolean setBlockData(int x, int y, int z, BlockData blockData) {
        Location loc = new Location(world, x, y, z);
        return setBlock(loc, blockData);
    }

    private boolean setBlock(Location loc, BlockData blockData) {
        if (Flags.TREES_GROWING_OUTSIDE_RANGE.isSetForWorld(world) || addon.getIslands().getProtectedIslandAt(loc).isPresent()) {
            loc.getBlock().setBlockData(blockData);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.bukkit.BlockChangeDelegate#getBlockData(int, int, int)
     */
    @Override
    public BlockData getBlockData(int x, int y, int z) {
        return world.getBlockAt(x, y, z).getBlockData();
    }

    /* (non-Javadoc)
     * @see org.bukkit.BlockChangeDelegate#getHeight()
     */
    @Override
    public int getHeight() {
        return world.getMaxHeight();
    }

    /* (non-Javadoc)
     * @see org.bukkit.BlockChangeDelegate#isEmpty(int, int, int)
     */
    @Override
    public boolean isEmpty(int x, int y, int z) {
        return world.getBlockAt(x, y, z).isEmpty();
    }

}
