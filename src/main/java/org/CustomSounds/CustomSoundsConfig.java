package org.CustomSounds;

import net.runelite.client.config.*;

@ConfigGroup("CustomDropSounds")
public interface CustomSoundsConfig extends Config
{

	@ConfigSection(
			name = "Value drops",
			description = "All the settings for drops",
			position = 8

	)
	String valueDrops = "valueDrops";

	@ConfigSection(
			name = "Clue drops",
			description = "All the settings for drops",
			position = 7

	)
	String clueDrops = "clueDrops";

	@ConfigSection(
			name = "Spec weapon sounds",
			description = "All the settings for Spec weapons",
			position = 9

	)
	String specWeapon = "specWeapon";
	@Range(max=100)
	@ConfigItem(
			keyName = "masterVolume",
			name = "Master Volume",
			description = "Sets the master volume of all ground item sounds",
			position = 0
	)
	default int masterVolume()
	{
		return 50;
	}

	@ConfigItem(
			keyName = "highlightSound",
			name = "Highlight Sound",
			description = "Configure whether or not to play a sound when a highlighted (ground item plugin) item appears",
			position = 1
	)
	default boolean highlightSound()
	{
		return false;
	}

	@ConfigItem(
			keyName = "petSound",
			name = "Pet Sound",
			description = "Configure whether or not to play a sound when a pet item appears",
			position = 2
	)
	default boolean petSound()
	{
		return false;
	}
		@ConfigItem(
			keyName = "maxHitBoolean",
			name = "Maxhit",
			description = "Enable/Disable Max Hit Sounds",
			section = "hitsplatToggleSection",
			position = 3
	)
	default boolean maxHitBoolean() {return false;}

	@ConfigItem(
			keyName = "announceDeath",
			name = "Death sound",
			description = "Enable/Disable Death Sound",
			position = 4
	)
	default boolean announceDeath() {
		return false;
	}

	@ConfigItem(
			keyName = "announceCollectionLog",
			name = "New collection log entry",
			description = " This one relies on you having chat messages (included with the popup option) enabled in game settings!",
			position = 5
	)
	default boolean announceCollectionLog() {
		return false;
	}

	@ConfigItem(
			keyName = "teleports",
			name = "Teleport Sounds",
			description = "Configure whether or not you want to use a custom sound for teleports (house tabs, capes, scrolls)",
			position = 6
	)
	default boolean teleports() {
		return false;
	}

	@ConfigItem(
			keyName = "beginnerClueSound",
			name = "Beginner Clue Sound",
			description = "Configure whether or not to play a sound when a beginner clue item appears",
			position = 0,
			section = clueDrops
	)

	default boolean beginnerClueSound()
	{
		return true;
	}

	@ConfigItem(
			keyName = "easyClueSound",
			name = "Easy Clue Sound",
			description = "Configure whether or not to play a sound when a easy clue item appears",
			position = 1,
			section = clueDrops
	)
	default boolean easyClueSound()
	{
		return true;
	}


	@ConfigItem(
			keyName = "mediumClueSound",
			name = "Medium Clue Sound",
			description = "Configure whether or not to play a sound when a medium clue item appears",
			position = 2,
			section = clueDrops
	)
	default boolean mediumClueSound()
	{
		return true;
	}

	@ConfigItem(
			keyName = "hardClueSound",
			name = "Hard Clue Sound",
			description = "Configure whether or not to play a sound when a hard clue item appears",
			position = 3,
			section = clueDrops
	)
	default boolean hardClueSound()
	{
		return true;
	}

	@ConfigItem(
			keyName = "eliteClueSound",
			name = "Elite Clue Sound",
			description = "Configure whether or not to play a sound when a elite clue item appears",
			position = 4,
			section = clueDrops
	)
	default boolean eliteClueSound()
	{
		return true;
	}

	@ConfigItem(
			keyName = "masterClueSound",
			name = "Master Clue Sound",
			description = "Configure whether or not to play a sound when a master clue item appears",
			position = 5,
			section = clueDrops
	)
	default boolean masterClueSound()
	{
		return true;
	}





	@ConfigItem(
			keyName = "lowestValueSound",
			name = "Lowest Value Sound",
			description = "Configure whether or not to play a sound when a lowest valued item appears",
			position = 0,
			section = valueDrops
	)
	default boolean lowestValueSound()
	{
		return false;
	}

	@ConfigItem(
			keyName = "lowestStart",
			name = "Lowest Value starting GP",
			description = "What is the starting gp you want to hear this sound" ,
			position = 1,
			section = valueDrops
	)
	default int lowestStart() {
		return 20000;
	}
	@ConfigItem(
			keyName = "lowestEnd",
			name = "Lowest Value ending GP",
			description = "What is the ending gp you want to hear this sound" ,
			position = 2,
			section = valueDrops
	)
	default int lowestEnd() {
		return 150000;
	}

	@ConfigItem(
			keyName = "lowValueSound",
			name = "Low Value Sound",
			description = "Configure whether or not to play a sound when a low valued item appears",
			position = 3,
			section = valueDrops
	)
	default boolean lowValueSound()
	{
		return false;
	}

	@ConfigItem(
			keyName = "lowStart",
			name = "Low Value starting GP",
			description = "What is the starting gp you want to hear this sound" ,
			position = 4,
			section = valueDrops
	)
	default int lowStart() {
		return 150000;
	}
	@ConfigItem(
			keyName = "lowEnd",
			name = "Low Value ending GP",
			description = "What is the ending gp you want to hear this sound" ,
			position = 5,
			section = valueDrops
	)
	default int lowEnd() {
		return 500000;
	}

	@ConfigItem(
			keyName = "mediumValueSound",
			name = "Medium Value Sound",
			description = "Configure whether or not to play a sound when a medium valued item appears",
			position = 6,
			section = valueDrops
	)
	default boolean mediumValueSound()
	{
		return false;
	}

	@ConfigItem(
			keyName = "mediumStart",
			name = "Medium Value starting GP",
			description = "What is the starting gp you want to hear this sound" ,
			position = 7,
			section = valueDrops
	)
	default int mediumStart() {
		return 500000;
	}
	@ConfigItem(
			keyName = "mediumEnd",
			name = "Medium Value ending GP",
			description = "What is the ending gp you want to hear this sound" ,
			position = 8,
			section = valueDrops
	)
	default int mediumEnd() {
		return 1000000;
	}

	@ConfigItem(
			keyName = "highValueSound",
			name = "High Value Sound",
			description = "Configure whether or not to play a sound when a high valued item appears",
			position = 9,
			section = valueDrops
	)
	default boolean highValueSound()
	{
		return false;
	}

	@ConfigItem(
			keyName = "highStart",
			name = "High Value starting GP",
			description = "What is the starting gp you want to hear this sound" ,
			position = 10,
			section = valueDrops
	)
	default int highStart() {
		return 1000000;
	}
	@ConfigItem(
			keyName = "highEnd",
			name = "High Value ending GP",
			description = "What is the ending gp you want to hear this sound" ,
			position = 11,
			section = valueDrops
	)
	default int highEnd() {
		return 10000000;
	}

	@ConfigItem(
			keyName = "highestValueSound",
			name = "Highest Value Sound",
			description = "Configure whether or not to play a sound when a highest valued item appears",
			position = 12,
			section = valueDrops
	)
	default boolean highestValueSound()
	{
		return false;
	}

	@ConfigItem(
			keyName = "highestStart",
			name = "Highest Value starting GP",
			description = "What is the starting gp you want to hear this sound" ,
			position = 13,
			section = valueDrops
	)
	default int highestStart() {
		return 10000000;
	}
	@ConfigItem(
			keyName = "highestEnd",
			name = "Highest Value ending GP",
			description = "What is the ending gp you want to hear this sound" ,
			position = 14,
			section = valueDrops
	)
	default int highestEnd() {
		return 1000000000;
	}

	@ConfigItem(
			keyName = "zaryteCrossBow",
			name = "Zaryte CrossBow Spec",
			description = "Configure whether or not you want to use a custom Zaryte CrossBow spec sound" ,
			position = 0,
			section = specWeapon
	)
	default boolean zaryteCrossBow() {
		return false;
	}

	@ConfigItem(
			keyName = "elderMaul",
			name = "Elder Maul Spec",
			description = "Configure whether or not you want to use a custom Elder Maul spec sound" ,
			position = 1,
			section = specWeapon
	)
	default boolean elderMaul() {
		return false;
	}

	@ConfigItem(
			keyName = "dragonClaws",
			name = "Dragon Claw Spec",
			description = "Configure whether or not you want to use a custom Dragon Claw spec sound" ,
			position = 2,
			section = specWeapon
	)
	default boolean dragonClaws() {
		return false;
	}

	@ConfigItem(
			keyName = "burnClaw",
			name = "Burn Claw Spec",
			description = "Configure whether or not you want to use a custom Burn Claw spec sound",
			position = 3,
			section = specWeapon
	)
	default boolean burnClaw() {
		return false;
	}

	@ConfigItem(
			keyName = "horn",
			name = "Horn Spec",
			description = "Configure whether or not you want to use a custom Horn spec sound",
			position = 4,
			section = specWeapon
	)
	default boolean horn() {
		return false;
	}

	@ConfigItem(
			keyName = "voidwaker",
			name = "Voidwaker Spec",
			description = "Configure whether or not you want to use a custom Voidwaker spec sound",
			position = 5,
			section = specWeapon
	)
	default boolean voidwaker() {
		return false;
	}
	@ConfigItem(
			keyName = "blowpipe",
			name = "Blowpipe Spec",
			description = "Configure whether or not you want to use a custom Blowpipe spec sound",
			position = 6,
			section = specWeapon
	)
	default boolean blowpipe() {
		return false;
	}
	@ConfigItem(
			keyName = "keris",
			name = "Keris Spec",
			description = "Configure whether or not you want to use a custom Keris spec sound",
			position = 7,
			section = specWeapon
	)
	default boolean keris() {
		return false;
	}

	@ConfigItem(
			keyName = "bgs",
			name = "BGS Spec",
			description = "Configure whether or not you want to use a custom BGS spec sound",
			position = 8,
			section = specWeapon
	)
	default boolean bgs() {
		return false;
	}

	@ConfigItem(
			keyName = "crystalHally",
			name = "Crystal Halberd Spec",
			description = "Configure whether or not you want to use a custom Crystal Halberd spec sound",
			position = 9,
			section = specWeapon
	)
	default boolean crystalHally() {
		return false;
	}


}
