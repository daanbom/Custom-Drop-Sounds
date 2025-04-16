package org.CustomSounds;
import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.RuneLite;
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

	@Provides
	CustomSoundsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CustomSoundsConfig.class);
	}

	@Inject
	private GroundItemsConfig groundItemsConfig;


	@Inject
	private Client client;
	@Inject
	private ItemManager itemManager;

	private static final String COLLOG = "New item added to your collection log";

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
			DIED_SOUND_FILE
	};


	private List<String> highlightedItemsList = new CopyOnWriteArrayList<>();
	private static final long CLIP_TIME_UNLOADED = -2;


	private SoundManager soundManager;
	private Clip clip = null;

	@Override
	protected void startUp()
	{
		initSoundFiles();
		updateHighlightedItemsList();
		soundManager = new SoundManager(config);
	}

	@Override
	protected void shutDown()
	{
		if (soundManager != null) {
			soundManager.shutdown();
		}
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
		Actor actor = event.getActor();;
		// Skip if actor is null
		if (actor == null)
		{
			return;
		}
		if (config.maxHitBoolean() && event.getHitsplat().getHitsplatType()==HitsplatID.DAMAGE_MAX_ME){
			playSound(MAX_SOUND_FILE);
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
		soundManager.playSound(f);
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

