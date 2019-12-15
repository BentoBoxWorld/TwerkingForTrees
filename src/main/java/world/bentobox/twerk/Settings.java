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

    @ConfigComment("Toggle on/off the sounds.")
    @ConfigEntry(path = "sounds.enabled")
    private boolean soundsEnabled = true;

    @ConfigComment("Sound that plays when the player twerked enough for the sapling to start growing faster.")
    @ConfigComment("Available sounds are the following:")
    @ConfigComment("   https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html")
    @ConfigEntry(path = "sounds.twerk.sound")
    private Sound soundsTwerkSound = Sound.BLOCK_NOTE_BLOCK_BASS;

    @ConfigEntry(path = "sounds.twerk.volume")
    private float soundsTwerkVolume = 1.0F;

    @ConfigEntry(path = "sounds.twerk.pitch")
    private float soundsTwerkPitch = 2.0F;

    @ConfigComment("Sound that plays when a small tree (1x1) grows.")
    @ConfigComment("Available sounds are the following:")
    @ConfigComment("   https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html")
    @ConfigEntry(path = "sounds.growing-small-tree.sound")
    private Sound soundsGrowingSmallTreeSound = Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT;

    @ConfigEntry(path = "sounds.growing-small-tree.volume")
    private float soundsGrowingSmallTreeVolume = 1.0F;

    @ConfigEntry(path = "sounds.growing-small-tree.pitch")
    private float soundsGrowingSmallTreePitch = 1.0F;

    @ConfigComment("Sound that plays when a big tree (2x2) grows.")
    @ConfigComment("Available sounds are the following:")
    @ConfigComment("   https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html")
    @ConfigEntry(path = "sounds.growing-big-tree.sound")
    private Sound soundsGrowingBigTreeSound = Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT;

    @ConfigEntry(path = "sounds.growing-big-tree.volume")
    private float soundsGrowingBigTreeVolume = 1.0F;

    @ConfigEntry(path = "sounds.growing-big-tree.pitch")
    private float soundsGrowingBigTreePitch = 1.0F;

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

    public float getSoundsTwerkVolume() {
        return soundsTwerkVolume;
    }

    public void setSoundsTwerkVolume(float soundsTwerkVolume) {
        this.soundsTwerkVolume = soundsTwerkVolume;
    }

    public float getSoundsTwerkPitch() {
        return soundsTwerkPitch;
    }

    public void setSoundsTwerkPitch(float soundsTwerkPitch) {
        this.soundsTwerkPitch = soundsTwerkPitch;
    }

    public Sound getSoundsGrowingSmallTreeSound() {
        return soundsGrowingSmallTreeSound;
    }

    public void setSoundsGrowingSmallTreeSound(Sound soundsGrowingSmallTreeSound) {
        this.soundsGrowingSmallTreeSound = soundsGrowingSmallTreeSound;
    }

    public float getSoundsGrowingSmallTreeVolume() {
        return soundsGrowingSmallTreeVolume;
    }

    public void setSoundsGrowingSmallTreeVolume(float soundsGrowingSmallTreeVolume) {
        this.soundsGrowingSmallTreeVolume = soundsGrowingSmallTreeVolume;
    }

    public float getSoundsGrowingSmallTreePitch() {
        return soundsGrowingSmallTreePitch;
    }

    public void setSoundsGrowingSmallTreePitch(float soundsGrowingSmallTreePitch) {
        this.soundsGrowingSmallTreePitch = soundsGrowingSmallTreePitch;
    }

    public Sound getSoundsGrowingBigTreeSound() {
        return soundsGrowingBigTreeSound;
    }

    public void setSoundsGrowingBigTreeSound(Sound soundsGrowingBigTreeSound) {
        this.soundsGrowingBigTreeSound = soundsGrowingBigTreeSound;
    }

    public float getSoundsGrowingBigTreeVolume() {
        return soundsGrowingBigTreeVolume;
    }

    public void setSoundsGrowingBigTreeVolume(float soundsGrowingBigTreeVolume) {
        this.soundsGrowingBigTreeVolume = soundsGrowingBigTreeVolume;
    }

    public float getSoundsGrowingBigTreePitch() {
        return soundsGrowingBigTreePitch;
    }

    public void setSoundsGrowingBigTreePitch(float soundsGrowingBigTreePitch) {
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
}
