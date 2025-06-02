package org.CustomSounds;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.RuneLite;
import net.runelite.client.audio.AudioPlayer;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.grounditems.GroundItemsConfig;
import net.runelite.client.plugins.grounditems.GroundItemsPlugin;
import net.runelite.client.plugins.loottracker.LootReceived;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
		name = "Custom Sounds",
		description = "Play custom sound effects for item drops, pets, hits and more",
		tags = {"sound", "effect", "item", "drop", "hit", "combat"}
)
@PluginDependency(GroundItemsPlugin.class)
public class CustomSoundsPlugin extends Plugin
{
	@Inject
	private CustomSoundsConfig config;

	@Inject
	private AudioPlayer audioPlayer;


	@Provides
	CustomSoundsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CustomSoundsConfig.class);
	}

	@Inject
	private GroundItemsConfig groundItemsConfig;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private Client client;
	@Inject
	private ItemManager itemManager;

	private Map<Integer, WeaponSound> allWeapons = new HashMap<>();

	private static final Pattern COLLECTION_LOG_ITEM_REGEX = Pattern.compile("New item added to your collection log:.*");


	private static final ImmutableList<String> PET_MESSAGES = ImmutableList.of("You have a funny feeling like you're being followed",
			"You feel something weird sneaking into your backpack",
			"You have a funny feeling like you would have been followed");
	private static final File CUSTOM_SOUNDS_DIR = new File(RuneLite.RUNELITE_DIR.getPath() + File.separator + "custom-drop-sounds");

	private static final File HIGHLIGHTED_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "highlighted_sound.wav");
	private static final File BEGINNER_CLUE_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "beginner_clue_sound.wav");
	private static final File EASY_CLUE_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "easy_clue_sound.wav");
	private static final File MEDIUM_CLUE_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "medium_clue_sound.wav");
	private static final File HARD_CLUE_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "hard_clue_sound.wav");
	private static final File ELITE_CLUE_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "elite_clue_sound.wav");
	private static final File MASTER_CLUE_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "master_clue_sound.wav");
	private static final File LOW_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "low_sound.wav");
	private static final File MEDIUM_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "medium_sound.wav");

	private static final File PET_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "pet_sound.wav");
	private static final File HIGH_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "high_sound.wav");
	private static final File HIGHEST_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "highest_sound.wav");
	private static final File LOWEST_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "lowest_sound.wav");
	private static final File COLLECTIONLOG_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "CollectionLog.wav");
	private static final File DIED_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "Died.wav");

	private static final File MAX_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "Max.wav");

	private static final File ZARYTE_CROSSBOW_SPEC_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "zaryte_spec.wav");
	private static final File ELDER_MAUL_SPEC_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "elder_maul.wav");
	private static final File DRAGON_CLAWS_SPEC_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "dragon_claws.wav");
	private static final File BURN_CLAW_SPEC_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "burn_claws.wav");
	private static final File HORN_SPEC_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "horn_spec.wav");
	private static final File VOIDWAKER_SPEC_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "voidwaker.wav");
	private static final File BLOWPIPE_SPEC_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "blowpipe.wav");
	private static final File KERIS_SPEC_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "keris_spec.wav");
	private static final File BGS_SPEC_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "bgs_spec.wav");
	private static final File CRYSTAL_HALLY_SPEC_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "crystal_hally_spec.wav");
	private static final File TELEPORT_SOUND_FILE = new File(CUSTOM_SOUNDS_DIR, "teleport.wav");
	private static final File[] SOUND_FILES = new File[]{
			HIGHLIGHTED_SOUND_FILE,
			BEGINNER_CLUE_SOUND_FILE,
			EASY_CLUE_SOUND_FILE,
			MEDIUM_CLUE_SOUND_FILE,
			HARD_CLUE_SOUND_FILE,
			ELITE_CLUE_SOUND_FILE,
			MASTER_CLUE_SOUND_FILE,
			LOW_SOUND_FILE,
			MEDIUM_SOUND_FILE,
			LOWEST_SOUND_FILE,
			HIGH_SOUND_FILE,
			HIGHEST_SOUND_FILE,
			PET_SOUND_FILE,
			MAX_SOUND_FILE,
			COLLECTIONLOG_SOUND_FILE,
			DIED_SOUND_FILE,
			ZARYTE_CROSSBOW_SPEC_SOUND_FILE,
			ELDER_MAUL_SPEC_SOUND_FILE,
			DRAGON_CLAWS_SPEC_SOUND_FILE,
			BURN_CLAW_SPEC_SOUND_FILE,
			HORN_SPEC_SOUND_FILE,
			VOIDWAKER_SPEC_SOUND_FILE,
			BLOWPIPE_SPEC_SOUND_FILE,
			KERIS_SPEC_SOUND_FILE,
			BGS_SPEC_SOUND_FILE,
			CRYSTAL_HALLY_SPEC_SOUND_FILE,
			TELEPORT_SOUND_FILE
	};


	private List<String> highlightedItemsList = new CopyOnWriteArrayList<>();

	private Clip clip = null;

	@Override
	protected void startUp()
	{
		initSoundFiles();
		updateHighlightedItemsList();
		initAllWeapons();
	}

	private void initAllWeapons()
	{
		// Animation-based weapons
		allWeapons.put(AnimationIDs.ELDER_MAUL.Id,
				new WeaponSound(ELDER_MAUL_SPEC_SOUND_FILE, () -> config.elderMaul(), 2, true));
		allWeapons.put(AnimationIDs.DRAGON_CLAW.Id,
				new WeaponSound(DRAGON_CLAWS_SPEC_SOUND_FILE, () -> config.dragonClaws(), 1, true));
		allWeapons.put(AnimationIDs.HORN_HIT.Id,
				new WeaponSound(HORN_SPEC_SOUND_FILE, () -> config.horn(), 0, true));
		allWeapons.put(AnimationIDs.HORN_MISS.Id,
				new WeaponSound(HORN_SPEC_SOUND_FILE, () -> config.horn(), 0, true));
		allWeapons.put(AnimationIDs.BURN_CLAW.Id,
				new WeaponSound(BURN_CLAW_SPEC_SOUND_FILE, () -> config.burnClaw(), 0, true));
		allWeapons.put(AnimationIDs.VOIDWAKER.Id,
				new WeaponSound(VOIDWAKER_SPEC_SOUND_FILE, () -> config.voidwaker(), 1, true));
		allWeapons.put(AnimationIDs.KERIS.Id,
				new WeaponSound(KERIS_SPEC_SOUND_FILE, () -> config.keris(), 1, true));
		allWeapons.put(AnimationIDs.BGS.Id,
				new WeaponSound(BGS_SPEC_SOUND_FILE, () -> config.bgs(), 1, true));
		allWeapons.put(AnimationIDs.CRYSTAL_HALLY.Id,
				new WeaponSound(CRYSTAL_HALLY_SPEC_SOUND_FILE, () -> config.crystalHally(), 1, true));

		// Teleports - all use same sound and config
		allWeapons.put(AnimationIDs.HOUSE_TABS.Id,
				new WeaponSound(TELEPORT_SOUND_FILE, () -> config.teleports(), 1, true));
		allWeapons.put(AnimationIDs.CAPES.Id,
				new WeaponSound(TELEPORT_SOUND_FILE, () -> config.teleports(), 1, true));
		allWeapons.put(AnimationIDs.SCROLLS.Id,
				new WeaponSound(TELEPORT_SOUND_FILE, () -> config.teleports(), 1, true));

		// Sound-based weapons
		allWeapons.put(SoundIDs.BLOWPIPE.Id,
				new WeaponSound(BLOWPIPE_SPEC_SOUND_FILE, () -> config.blowpipe(), 1, false));
		allWeapons.put(SoundIDs.RUBY_BOLT.Id,
				new WeaponSound(ZARYTE_CROSSBOW_SPEC_SOUND_FILE, () -> config.zaryteCrossBow(), 1, false));
	}


	@Override
	protected void shutDown()
	{
		highlightedItemsList = null;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{

		if (configChanged.getGroup().equals("grounditems") && configChanged.getKey().equals("highlightedItems"))
		{
			updateHighlightedItemsList();
		}
	}

	@Subscribe
	public void onLootReceived(LootReceived lootReceived) {
		for (ItemStack stack : lootReceived.getItems()) {
			handleItem(stack.getId(), stack.getQuantity());
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		Actor actor = event.getActor();
		// Skip if actor is null
		if (actor == null)
		{
			return;
		}
		if (config.maxHitBoolean() && event.getHitsplat().getHitsplatType()==HitsplatID.DAMAGE_MAX_ME){
			playSound(MAX_SOUND_FILE);
		}

	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		Actor actor = event.getActor();

		if (actor != client.getLocalPlayer())
		{
			return;
		}
		int currentAnimationId = actor.getAnimation();

		WeaponSound weapon = allWeapons.get(currentAnimationId);
		if (weapon != null && weapon.isAnimationBased && weapon.configCheck.get())
		{
			// Schedule sound to play after specified delay
			scheduleDelayedSound(weapon.soundFile, weapon.delay);
		}
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed event)
	{
		int soundId = event.getSoundId();

		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return;
		}

		int currentAnimation = localPlayer.getAnimation();

		// Check if this sound should be blocked (animation-based weapons)
		WeaponSound animationWeapon = allWeapons.get(currentAnimation);
		if (animationWeapon != null && animationWeapon.isAnimationBased && animationWeapon.configCheck.get())
		{
			event.consume(); // Block original sound
			return;
		}
		// Check if this is a sound-based weapon
		WeaponSound soundWeapon = allWeapons.get(soundId);
		if (soundWeapon != null && !soundWeapon.isAnimationBased && soundWeapon.configCheck.get())
		{
			event.consume();

			if (soundWeapon.delay > 0)
			{
				scheduleDelayedSound(soundWeapon.soundFile, soundWeapon.delay);
			}
			else
			{
				playSound(soundWeapon.soundFile);
			}
		}
	}

	private void handleItem(int id, int quantity) {
		final ItemComposition itemComposition = itemManager.getItemComposition(id);
		final String name = itemComposition.getName().toLowerCase();

		if (config.highlightSound() && highlightedItemsList.contains(name))
		{
			playSound(HIGHLIGHTED_SOUND_FILE);
			return;
		}

		final int gePrice = itemManager.getItemPrice(id) * quantity;
		final int haPrice = itemComposition.getHaPrice() * quantity;
		final int value = getValueByMode(gePrice, haPrice);

		if (config.beginnerClueSound() && name.contains("clue scroll (beginner)")){
			playSound(BEGINNER_CLUE_SOUND_FILE);
		}
		if (config.easyClueSound() && name.contains("clue scroll (easy)")){
			playSound(EASY_CLUE_SOUND_FILE);
		}
		if (config.mediumClueSound() && name.contains("clue scroll (medium)")){
			playSound(MEDIUM_CLUE_SOUND_FILE);
		}
		if (config.hardClueSound() && name.contains("clue scroll (hard)")){
			playSound(HARD_CLUE_SOUND_FILE);
		}

		if (config.eliteClueSound() && name.contains("clue scroll (elite)")){
			playSound(ELITE_CLUE_SOUND_FILE);
		}
		if (config.masterClueSound() && name.contains("clue scroll (master)")){
			playSound(MASTER_CLUE_SOUND_FILE);
		}



		if (config.lowestValueSound() && value >= config.lowestStart() && value < config.lowestEnd())
		{
			playSound(LOWEST_SOUND_FILE);
		}
		if (config.lowValueSound() && value >= config.lowStart() && value < config.lowEnd())
		{
			playSound(LOW_SOUND_FILE);
		}
		if (config.mediumValueSound() && value >= config.mediumStart() && value < config.mediumEnd())
		{
			playSound(MEDIUM_SOUND_FILE);
		}
		if (config.highValueSound() && value >= config.highStart() && value < config.highEnd())
		{
			playSound(HIGH_SOUND_FILE);
		}
		if (config.highestValueSound() && value >= config.highestStart() && value < config.highestEnd())
		{
			playSound(HIGHEST_SOUND_FILE);
		}
	}

	private void playSound(File f)
	{
		if (!f.exists()) {
			log.warn("Sound file does not exist: {}", f.getPath());
			return;
		}
		try {
			// Convert percentage (0-100) to gain in dB
			float gainInDecibels = convertToDecibels(config.masterVolume());

			// Call the instance method
			audioPlayer.play(f, gainInDecibels);
		} catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
			log.warn("Error playing sound {}: {}", f.getName(), e.getMessage());
		}

	}
	private void scheduleDelayedSound(File soundFile, int delayTicks)
	{
		// Convert ticks to milliseconds (1 tick = 600ms)
		int delayMs = delayTicks * 600;

		executor.schedule(() -> {
			playSound(soundFile); // Use your existing playSound method
		}, delayMs, TimeUnit.MILLISECONDS);
	}



	private float convertToDecibels(int volumePercentage) {
		// Avoid log(0)
		if (volumePercentage <= 0) {
			return -80.0f; // Very quiet
		}

		// Convert percentage to a ratio (0.0 to 1.0)
		float ratio = volumePercentage / 100.0f;

		// Convert ratio to decibels
		return 20f * (float) Math.log10(ratio);
	}

	// sets volume using dB to linear conversion
	private void setVolume(int volume)
	{
		float vol = volume/100.0f;
		vol *= config.masterVolume()/100.0f;
		FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(20.0f * (float) Math.log10(vol));
	}

	// initialize sound files if they haven't been created yet
	private void initSoundFiles()
	{
		if (!CUSTOM_SOUNDS_DIR.exists())
		{
			CUSTOM_SOUNDS_DIR.mkdirs();
		}

		for (File f : SOUND_FILES)
		{
			try
			{
				if (f.exists()) {
					continue;
				}
				InputStream stream = CustomSoundsPlugin.class.getClassLoader().getResourceAsStream(f.getName());
				OutputStream out = new FileOutputStream(f);
				byte[] buffer = new byte[8 * 1024];
				int bytesRead;
				while ((bytesRead = stream.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
				out.close();
				stream.close();
			}  catch (Exception e) {
				log.debug(e + ": " + f);
			}
		}

	}

	private int getValueByMode(int gePrice, int haPrice)
	{
		switch (groundItemsConfig.valueCalculationMode())
		{
			case GE:
				return gePrice;
			case HA:
				return haPrice;
			default: // Highest
				return Math.max(gePrice, haPrice);
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath actorDeath) {
		if (actorDeath.getActor() != client.getLocalPlayer()){
			return;
		}
		if (config.announceDeath()) {
			playSound(DIED_SOUND_FILE);
		}
	}



	private void updateHighlightedItemsList()
	{
		if (!groundItemsConfig.getHighlightItems().isEmpty())
		{
			highlightedItemsList = Text.fromCSV(groundItemsConfig.getHighlightItems().toLowerCase());
		}
	}
	@Subscribe
	public void onChatMessage(ChatMessage event) {
		String chatMessage = event.getMessage();
		if (event.getType() != ChatMessageType.GAMEMESSAGE
				&& event.getType() != ChatMessageType.SPAM
				&& event.getType() != ChatMessageType.TRADE
				&& event.getType() != ChatMessageType.FRIENDSCHATNOTIFICATION)
		{
			return;
		}
		if (config.announceCollectionLog() && COLLECTION_LOG_ITEM_REGEX.matcher(event.getMessage()).matches()){
			playSound(COLLECTIONLOG_SOUND_FILE);
		}
		else if (config.petSound() && PET_MESSAGES.stream().anyMatch(chatMessage::contains))
		{
			playSound(PET_SOUND_FILE);
		}

	}

}

