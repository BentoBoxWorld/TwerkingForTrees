package world.bentobox.twerk;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.twerk.events.TreeGrowListener;

public final class TwerkingForTrees extends Addon {

    @Override
    public void onLoad() {
        // Nothing to do
    }

    @Override
    public void onEnable() {
        // Register listener
        registerListener(new TreeGrowListener(this));
    }

    @Override
    public void onDisable() {
        // Nothing to do here
    }

}
