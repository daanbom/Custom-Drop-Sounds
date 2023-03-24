package org.AlteredSound;
import com.google.inject.Provides;
import java.util.HashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;

import net.runelite.api.annotations.Varbit;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
		name = "Altered Drop Sounds",
		description = "Custom sounds when you get drops",
		tags = {"sound", "drop", "pet", "clip","announce"}
)



public class AlteredSounds extends Plugin
{
	@Inject
	private Client client;

	@Getter(AccessLevel.PACKAGE)
	@Inject
	private ClientThread clientThread;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private SoundEngine soundEngine;
	@Inject
	private AlteredSoundsConfig config;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private OkHttpClient okHttpClient;

	private static final int ID_OBJECT_LUMCASTLE_GROUND_LEVEL_STAIRCASE = 16671;
	private static final int WORLD_POINT_LUMCASTLE_STAIRCASE_NORTH_X = 3204;
	private static final int WORLD_POINT_LUMCASTLE_STAIRCASE_NORTH_Y = 3229;

	private static final String ZULRAH = "Zulrah";
	private static final String C_ENGINEER = "C Engineer";
	private static final Pattern KILLCOUNT_PATTERN = Pattern.compile("Your (?:completion count for |subdued |completed )?(.+?) (?:(?:kill|harvest|lap|completion) )?(?:count )?is: <col=ff0000>(\\d+)</col>");
	private static final Pattern NEW_PB_PATTERN = Pattern.compile("(?i)(?:(?:Fight |Lap |Challenge |Corrupted challenge )?duration:|Subdued in) <col=[0-9a-f]{6}>(?<pb>[0-9:]+(?:\\.[0-9]+)?)</col> \\(new personal best\\)");
	private static final Pattern STRAY_DOG_GIVEN_BONES_REGEX = Pattern.compile("You give the dog some nice.*bones.*");
	private static final Pattern COLLECTION_LOG_ITEM_REGEX = Pattern.compile("New item added to your collection log:.*");
	private static final Pattern COMBAT_TASK_REGEX = Pattern.compile("Congratulations, you've completed an? (?:\\w+) combat task:.*");
	private static final Pattern QUEST_REGEX = Pattern.compile("Congratulations, you've completed a quest:.*");
	private static final Pattern BOND_OFFER_REGEX = Pattern.compile(C_ENGINEER + " is offering to give you a bond\\.");
	private static final Random random = new Random();

	private int lastLoginTick = -1;

	private boolean gameStateLoggedIn = false;
	@Override
	protected void startUp() throws Exception
	{
		lastLoginTick = -1;
		executor.submit(() -> {
		SoundFileManager.ensureDownloadDirectoryExists();
		SoundFileManager.downloadAllMissingSounds(okHttpClient);
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		soundEngine.close();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		gameStateLoggedIn = event.getGameState() == GameState.LOGGED_IN;
		switch(event.getGameState())
		{
			case LOGIN_SCREEN:
			case HOPPING:
			case LOGGING_IN:
			case LOGIN_SCREEN_AUTHENTICATOR:
			case CONNECTION_LOST:
				// set to -1 here in-case of race condition with varbits changing before this handler is called
				// when game state becomes LOGGED_IN
				lastLoginTick = -1;
				break;
			case LOGGED_IN:
				lastLoginTick = client.getTickCount();
				break;
		}

	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
		if (config.low() && menuOptionClicked.getId() == ID_OBJECT_LUMCASTLE_GROUND_LEVEL_STAIRCASE &&
				menuOptionClicked.getMenuOption().equals("Climb-up")) {
			WorldPoint wp = WorldPoint.fromLocal(client, LocalPoint.fromScene(menuOptionClicked.getParam0(), menuOptionClicked.getParam1()));
			if (wp.getX() == WORLD_POINT_LUMCASTLE_STAIRCASE_NORTH_X && wp.getY() == WORLD_POINT_LUMCASTLE_STAIRCASE_NORTH_Y) {
				// Now we know this is the northern staircase only in lumbridge castle ground floor
				soundEngine.playClip(Sound.EASTER_EGG_STAIRCASE);
			}
		}
	}
	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE && chatMessage.getType() != ChatMessageType.SPAM) {
			return;
		}

		if (config.low() && COLLECTION_LOG_ITEM_REGEX.matcher(chatMessage.getMessage()).matches()) {
			soundEngine.playClip(Sound.COLLECTION_LOG_SLOT);

		} else if (config.lowest() && QUEST_REGEX.matcher(chatMessage.getMessage()).matches()) {
			soundEngine.playClip(Sound.QUEST);

		} else if (config.medium() && COMBAT_TASK_REGEX.matcher(chatMessage.getMessage()).matches()) {
			soundEngine.playClip(Sound.COMBAT_TASK);
		}


	}


	@Provides
	AlteredSoundsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AlteredSoundsConfig.class);
	}
}
