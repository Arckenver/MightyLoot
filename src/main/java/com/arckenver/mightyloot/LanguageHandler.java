package com.arckenver.mightyloot;

import java.io.File;
import java.io.IOException;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class LanguageHandler
{
	private static File languageFile;
	private static ConfigurationLoader<CommentedConfigurationNode> languageManager;
	private static CommentedConfigurationNode language;
	
	public static void init(File rootDir)
	{
		languageFile = new File(rootDir, "language.conf");
	}
	
	public static void load()
	{
		languageManager = HoconConfigurationLoader.builder().setPath(languageFile.toPath()).build();
		try
		{
			if (!languageFile.exists())
			{
				languageFile.getParentFile().mkdirs();
				languageFile.createNewFile();
				language = languageManager.load();
				languageManager.save(language);
			}
			language = languageManager.load();
		}
		catch (IOException e)
		{
			MightyLootPlugin.getLogger().error("Could not load or create language file !");
			e.printStackTrace();
		}
		
		check(language.getNode("AA"), "You must precise a world name");
		check(language.getNode("AB"), "Loots are at position {POS}");
		check(language.getNode("AC"), "There is currently no loot in this world");
		check(language.getNode("AD"), "You must be an in game player to perform that command");
		check(language.getNode("AE"), "Config and language files has been reloaded, see console if an error occured");
		check(language.getNode("AF"), "Could not find loot config for this world");
		check(language.getNode("AG"), "Could not find loots of this type in this world");
		
		check(language.getNode("BA"), "The {LOOT} has been found by {PLAYER} ");
		check(language.getNode("BB"), "The last {LOOT} in {WORLD} has now vanished !");
		check(language.getNode("BC"), "A new {LOOT} has spawned in {WORLD} !");
		check(language.getNode("BD"), "Type {CMD}");
		
		check(language.getNode("CA"), "gives you your distance to the loot in this world");
		check(language.getNode("CB"), "gives chest location");
		check(language.getNode("CC"), "forces the spawn of a new loot");
		check(language.getNode("CD"), "removes the loot from a world");
		check(language.getNode("CE"), "reloads the config and language files");
		
		check(language.getNode("DA"), "You must turn {DEGREES}° to the {DIRECTION} and the nearest loot will be at a distance of {DISTANCE} blocks");
		check(language.getNode("DB"), "right");
		check(language.getNode("DC"), "left");
		check(language.getNode("DD"), "click");
		
		save();
	}
	
	public static String get(String id)
	{
		return language.getNode(id).getString("TEXT_NOT_FOUND");
	}
	
	private static void check(CommentedConfigurationNode node, String def)
	{
		if (node.getString() == null)
		{
			node.setValue(def);
		}
	}
	
	private static void save()
	{
		try
		{
			languageManager.save(language);
		}
		catch (IOException e)
		{
			MightyLootPlugin.getLogger().error("Could not load or create language file !");
			e.printStackTrace();
		}
	}
}
