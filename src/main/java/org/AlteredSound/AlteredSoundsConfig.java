package org.AlteredSound;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("example")
public interface AlteredSoundsConfig extends Config
{
	String GROUP = "alteredsounds";
	@ConfigItem(
		keyName = "lowest",
		name = "50k - 150k",
		description = "Do you want a sound to play when you receive a drop between 50-250k" ,
			position = 0
	)
	default boolean lowest() {
		return false;
	}

	@ConfigItem(
			keyName = "low",
			name = "150k - 500k",
			description = "Do you want a sound to play when you receive a drop between 150k-500k" ,
			position = 1
	)
	default boolean low() {
		return true;
	}

	@ConfigItem(
			keyName = "medium",
			name = "Drop 500k - 1m",
			description = "Do you want a sound to play when you receive a drop between 500-1m" ,
			position = 2
	)
	default boolean medium() {
		return false;
	}

	@Range(
			min = 0,
			max = 200
	)
	@ConfigItem(
			keyName = "announcementVolume",
			name = "Announcement volume",
			description = "Adjust how loud the audio announcements are played!",
			position = 3
	)
	default int announcementVolume() {
		return 100;
	}

	default String greeting()
	{
		return "Hello";
	}
}
