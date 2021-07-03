package world.bentobox.twerk;

import org.bukkit.Sound;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;

/**
 * @since 1.1.0
 */
@ConfigComment("TwerkingForTrees v[version] configuration file.")
@ConfigComment("")
@StoreAt(filename="config.yml", path = "addons/TwerkingForTrees")
public class Settings implements ConfigObject {

    @ConfigComment("How many times the player must twerk before the tree start growing faster.")
    @ConfigComment("If the player has not twerked enough, then the tree will not grow faster.")
    @ConfigEntry(path = "minimum-twerks")
    private int minimumTwerks = 4;

    @ConfigComment("Range to look for saplings when twerking. A range of 5 will look +/- 5 blocks in all directions around the player")
    @ConfigComment("Making this too big will lag your server.")
    @ConfigEntry(path = "range")
    private int range = 5;

    @ConfigComment("Toggle on/off the sounds.")
    @ConfigEntry(path = "sounds.enabled")
    private boolean soundsEnabled = true;

    @ConfigComment("Sound that plays when the player twerked enough for the sapling to start growing faster.")
    @ConfigComment("Available sounds are the following:")
    @ConfigComment("   https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html")
    @ConfigEntry(path = "sounds.twerk.sound")
    private Sound soundsTwerkSound = Sound.BLOCK_NOTE_BLOCK_BASS;

    @ConfigEntry(path = "sounds.twerk.volume")
    private double soundsTwerkVolume = 1.0F;

    @ConfigEntry(path = "sounds.twerk.pitch")
    private double soundsTwerkPitch = 2.0F;

    @ConfigComment("Toggle on/off the particle effects.")
    @ConfigEntry(path = "effects.enabled")
    private boolean effectsEnabled = true;

    /**
     * @return the minimumTwerks
     */
    public int getMinimumTwerks() {
        return minimumTwerks;
    }

    /**
     * @param minimumTwerks the minimumTwerks to set
     */
    public void setMinimumTwerks(int minimumTwerks) {
        this.minimumTwerks = minimumTwerks;
    }

    /**
     * @return the soundsEnabled
     */
    public boolean isSoundsEnabled() {
        return soundsEnabled;
    }

    /**
     * @param soundsEnabled the soundsEnabled to set
     */
    public void setSoundsEnabled(boolean soundsEnabled) {
        this.soundsEnabled = soundsEnabled;
    }

    /**
     * @return the soundsTwerkSound
     */
    public Sound getSoundsTwerkSound() {
        return soundsTwerkSound;
    }

    /**
     * @param soundsTwerkSound the soundsTwerkSound to set
     */
    public void setSoundsTwerkSound(Sound soundsTwerkSound) {
        this.soundsTwerkSound = soundsTwerkSound;
    }

    /**
     * @return the soundsTwerkVolume
     */
    public double getSoundsTwerkVolume() {
        return soundsTwerkVolume;
    }

    /**
     * @param soundsTwerkVolume the soundsTwerkVolume to set
     */
    public void setSoundsTwerkVolume(double soundsTwerkVolume) {
        this.soundsTwerkVolume = soundsTwerkVolume;
    }

    /**
     * @return the soundsTwerkPitch
     */
    public double getSoundsTwerkPitch() {
        return soundsTwerkPitch;
    }

    /**
     * @param soundsTwerkPitch the soundsTwerkPitch to set
     */
    public void setSoundsTwerkPitch(double soundsTwerkPitch) {
        this.soundsTwerkPitch = soundsTwerkPitch;
    }

    /**
     * @return the effectsEnabled
     */
    public boolean isEffectsEnabled() {
        return effectsEnabled;
    }

    /**
     * @param effectsEnabled the effectsEnabled to set
     */
    public void setEffectsEnabled(boolean effectsEnabled) {
        this.effectsEnabled = effectsEnabled;
    }

    /**
     * @return the range
     */
    public int getRange() {
        if (range < 0) {
            range = 0;
        }
        return range;
    }

    /**
     * @param range the range to set
     */
    public void setRange(int range) {
        this.range = range;
    }
}
