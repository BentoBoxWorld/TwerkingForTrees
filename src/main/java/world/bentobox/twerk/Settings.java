package world.bentobox.twerk;

import org.bukkit.Effect;
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

    @ConfigComment("Hold to twerk. Accessibility feature. Instead of hitting the crouch button continuously, hold it down.")
    @ConfigEntry(path = "hold-for-twerk")
    private boolean holdForTwerk = false;
    
    @ConfigComment("Use sprinting to grow trees instead of twerking.")
    @ConfigEntry(path = "sprint-to-grow")
    private boolean sprintToGrow = false;

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

    @ConfigComment("Sound that plays when a small tree (1x1) grows.")
    @ConfigComment("Available sounds are the following:")
    @ConfigComment("   https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html")
    @ConfigEntry(path = "sounds.growing-small-tree.sound")
    private Sound soundsGrowingSmallTreeSound = Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT;

    @ConfigEntry(path = "sounds.growing-small-tree.volume")
    private double soundsGrowingSmallTreeVolume = 1.0F;

    @ConfigEntry(path = "sounds.growing-small-tree.pitch")
    private double soundsGrowingSmallTreePitch = 1.0F;

    @ConfigComment("Sound that plays when a big tree (2x2) grows.")
    @ConfigComment("Available sounds are the following:")
    @ConfigComment("   https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html")
    @ConfigEntry(path = "sounds.growing-big-tree.sound")
    private Sound soundsGrowingBigTreeSound = Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT;

    @ConfigEntry(path = "sounds.growing-big-tree.volume")
    private double soundsGrowingBigTreeVolume = 1.0F;

    @ConfigEntry(path = "sounds.growing-big-tree.pitch")
    private double soundsGrowingBigTreePitch = 1.0F;

    @ConfigComment("Toggle on/off the particle effects.")
    @ConfigEntry(path = "effects.enabled")
    private boolean effectsEnabled = true;

    @ConfigComment("Effect that plays each time the player twerks.")
    @ConfigComment("Available effects are the following:")
    @ConfigComment("   https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Effect.html")
    @ConfigEntry(path = "effects.twerk")
    private Effect effectsTwerk = Effect.MOBSPAWNER_FLAMES;

    public int getMinimumTwerks() {
        return minimumTwerks;
    }

    public void setMinimumTwerks(int minimumTwerks) {
        this.minimumTwerks = minimumTwerks;
    }

    public boolean isSoundsEnabled() {
        return soundsEnabled;
    }

    public void setSoundsEnabled(boolean soundsEnabled) {
        this.soundsEnabled = soundsEnabled;
    }

    public Sound getSoundsTwerkSound() {
        return soundsTwerkSound;
    }

    public void setSoundsTwerkSound(Sound soundsTwerkSound) {
        this.soundsTwerkSound = soundsTwerkSound;
    }

    public double getSoundsTwerkVolume() {
        return soundsTwerkVolume;
    }

    public void setSoundsTwerkVolume(double soundsTwerkVolume) {
        this.soundsTwerkVolume = soundsTwerkVolume;
    }

    public double getSoundsTwerkPitch() {
        return soundsTwerkPitch;
    }

    public void setSoundsTwerkPitch(double soundsTwerkPitch) {
        this.soundsTwerkPitch = soundsTwerkPitch;
    }

    public Sound getSoundsGrowingSmallTreeSound() {
        return soundsGrowingSmallTreeSound;
    }

    public void setSoundsGrowingSmallTreeSound(Sound soundsGrowingSmallTreeSound) {
        this.soundsGrowingSmallTreeSound = soundsGrowingSmallTreeSound;
    }

    public double getSoundsGrowingSmallTreeVolume() {
        return soundsGrowingSmallTreeVolume;
    }

    public void setSoundsGrowingSmallTreeVolume(double soundsGrowingSmallTreeVolume) {
        this.soundsGrowingSmallTreeVolume = soundsGrowingSmallTreeVolume;
    }

    public double getSoundsGrowingSmallTreePitch() {
        return soundsGrowingSmallTreePitch;
    }

    public void setSoundsGrowingSmallTreePitch(double soundsGrowingSmallTreePitch) {
        this.soundsGrowingSmallTreePitch = soundsGrowingSmallTreePitch;
    }

    public Sound getSoundsGrowingBigTreeSound() {
        return soundsGrowingBigTreeSound;
    }

    public void setSoundsGrowingBigTreeSound(Sound soundsGrowingBigTreeSound) {
        this.soundsGrowingBigTreeSound = soundsGrowingBigTreeSound;
    }

    public double getSoundsGrowingBigTreeVolume() {
        return soundsGrowingBigTreeVolume;
    }

    public void setSoundsGrowingBigTreeVolume(double soundsGrowingBigTreeVolume) {
        this.soundsGrowingBigTreeVolume = soundsGrowingBigTreeVolume;
    }

    public double getSoundsGrowingBigTreePitch() {
        return soundsGrowingBigTreePitch;
    }

    public void setSoundsGrowingBigTreePitch(double soundsGrowingBigTreePitch) {
        this.soundsGrowingBigTreePitch = soundsGrowingBigTreePitch;
    }

    public boolean isEffectsEnabled() {
        return effectsEnabled;
    }

    public void setEffectsEnabled(boolean effectsEnabled) {
        this.effectsEnabled = effectsEnabled;
    }

    public Effect getEffectsTwerk() {
        return effectsTwerk;
    }

    public void setEffectsTwerk(Effect effectsTwerk) {
        this.effectsTwerk = effectsTwerk;
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

    /**
     * @return the holdForTwerk
     */
    public boolean isHoldForTwerk() {
        return holdForTwerk;
    }

    /**
     * @param holdForTwerk the holdForTwerk to set
     */
    public void setHoldForTwerk(boolean holdForTwerk) {
        this.holdForTwerk = holdForTwerk;
    }

    /**
     * @return the sprintToGrow
     */
    public boolean isSprintToGrow() {
        return sprintToGrow;
    }

    /**
     * @param sprintToGrow the sprintToGrow to set
     */
    public void setSprintToGrow(boolean sprintToGrow) {
        this.sprintToGrow = sprintToGrow;
    }
}
