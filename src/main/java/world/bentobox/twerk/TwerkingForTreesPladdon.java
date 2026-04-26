package world.bentobox.twerk;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;

public class TwerkingForTreesPladdon extends Pladdon {
    private Addon addon;

    @Override
    public Addon getAddon() {
        if (addon == null) {
            addon = new TwerkingForTrees();
        }
        return addon;
    }
}
