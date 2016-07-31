package com.arckenver.mightyloot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

import com.arckenver.mightyloot.object.LootConfig;
import com.arckenver.mightyloot.object.LootType;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigHandler
{
	private static File configFile;
	private static ConfigurationLoader<CommentedConfigurationNode> configManager;
	private static CommentedConfigurationNode config;
	
	private static ArrayList<LootConfig> lootConfigs;
	
	public static void init(File rootDir)
	{
		configFile = new File(rootDir, "config.conf");
		configManager = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();
		
		try
		{
			if (!configFile.exists())
			{
				configFile.getParentFile().mkdirs();
				configFile.createNewFile();
				config = configManager.load();
				
				setToDefaultConfig(config);
				
				configManager.save(config);
			}
			config = configManager.load();

		}
		catch (IOException e)
		{
			MightyLootPlugin.getLogger().error("Could not write in the config file !");
		}
	}
	
	public static void load()
	{
		lootConfigs = new ArrayList<LootConfig>();
		if (!config.getNode("lootTypes").hasMapChildren())
		{
			MightyLootPlugin.getLogger().warn("Could not find any loot type in config file !");
			return;
		}
		Hashtable<String, LootType> lootTypes = new Hashtable<String, LootType>();
		for (Entry<Object, ? extends CommentedConfigurationNode> entry : config.getNode("lootTypes").getChildrenMap().entrySet())
		{
			String id = entry.getKey().toString();
			String jsonDisplay = entry.getValue().getNode("display").getString();
			LootType lootType = new LootType(id, jsonDisplay);
			for (Entry<Object, ? extends CommentedConfigurationNode> e : entry.getValue().getNode("items").getChildrenMap().entrySet())
			{
				try
				{
					int min = Integer.parseInt(e.getValue().getString().split("-")[0]);
					int max = Integer.parseInt(e.getValue().getString().split("-")[1]);
					ItemType itemType = Sponge.getRegistry().getType(ItemType.class, e.getKey().toString()).get();
					lootType.addItem(itemType, min, max);
				}
				catch (NumberFormatException ex)
				{
					MightyLootPlugin.getLogger().warn("Invalid integer range in config file for \"" + id + "\"loot : " + e.getValue().getString());
				}
				catch (NoSuchElementException ex)
				{
					MightyLootPlugin.getLogger().warn("Invalid item type in config file for \"" + id + "\"loot : " + e.getKey().toString());
				}
			}
			lootTypes.put(id, lootType);
		}
		if (!config.getNode("worlds").hasMapChildren())
		{
			MightyLootPlugin.getLogger().warn("There is no world configured to have loots in config file !");
			return;
		}
		for (Entry<Object, ? extends CommentedConfigurationNode> entry : config.getNode("worlds").getChildrenMap().entrySet())
		{
			String worldName = entry.getKey().toString();
			if (Sponge.getServer().getWorld(worldName).isPresent())
			{
				CommentedConfigurationNode worldNode = config.getNode("worlds").getNode(worldName);
				LootConfig lootConfig = new LootConfig(
						worldName, 
						worldNode.getNode("frequency").getInt(),
						worldNode.getNode("minX").getInt(),
						worldNode.getNode("minY").getInt(),
						worldNode.getNode("minZ").getInt(),
						worldNode.getNode("maxX").getInt(),
						worldNode.getNode("maxY").getInt(),
						worldNode.getNode("maxY").getInt()
				);
				if (worldNode.getNode("lootTypes").hasMapChildren())
				{
					for (Entry<Object, ? extends CommentedConfigurationNode> e : worldNode.getNode("lootTypes").getChildrenMap().entrySet())
					{
						String lootId = e.getKey().toString();
						if (lootTypes.containsKey(lootId))
						{
							int probability = e.getValue().getInt();
							lootConfig.addLootType(lootTypes.get(lootId), probability);
						}
						else
						{
							MightyLootPlugin.getLogger().warn("Invalid loot type \"" + lootId + "\" for world \"" + worldName + "\" in config file !");
						}
					}
					if (!lootConfig.isEmpty())
					{
						lootConfigs.add(lootConfig);
					}
				}
				else
				{
					MightyLootPlugin.getLogger().warn("Could not find any loot for world " + worldName + " in config file !");
				}
			}
			else
			{
				MightyLootPlugin.getLogger().warn(worldName + " is not a valid world name !");
			}
		}
	}
	
	public static void saveConfig()
	{
		try
		{
			configManager.save(config);
		}
		catch (IOException e)
		{
			MightyLootPlugin.getLogger().error("Could not write in the config file !");
		}
	}
	
	public static void setToDefaultConfig(CommentedConfigurationNode conf)
	{
		CommentedConfigurationNode node;
		
		node = conf.getNode("lootTypes").getNode("mine");
		node.getNode("display").setValue("{\"text\":\"Mine Loot\",\"color\":\"aqua\"}");
		node.getNode("items").getNode("minecraft:emerald").setValue("1-6");
		node.getNode("items").getNode("minecraft:diamond").setValue("2-8");
		node.getNode("items").getNode("minecraft:gold_ingot").setValue("5-16");
		node.getNode("items").getNode("minecraft:iron_ingot").setValue("16-64");
		node.getNode("items").getNode("minecraft:coal").setValue("16-64");
		node.getNode("items").getNode("minecraft:redstone").setValue("0-128");
		node.getNode("items").getNode("minecraft:flint").setValue("0-32");
		node.getNode("items").getNode("minecraft:stone").setValue("32-128");
		node.getNode("items").getNode("minecraft:dirt").setValue("0-64");
		node.getNode("items").getNode("minecraft:gravel").setValue("0-32");
		
		node = conf.getNode("lootTypes").getNode("nether");
		node.getNode("display").setValue("{\"text\":\"Nether Loot\",\"color\":\"dark_purple\"}");
		node.getNode("items").getNode("minecraft:netherrack").setValue("64-128");
		node.getNode("items").getNode("minecraft:nether_brick").setValue("32-128");
		node.getNode("items").getNode("minecraft:nether_wart").setValue("0-32");
		node.getNode("items").getNode("minecraft:soul_sand").setValue("0-64");
		node.getNode("items").getNode("minecraft:glowstone").setValue("16-64");
		node.getNode("items").getNode("minecraft:quartz").setValue("64-256");

		node = conf.getNode("lootTypes").getNode("material");
		node.getNode("display").setValue("{\"text\":\"Material Loot\",\"color\":\"gray\"}");
		node.getNode("items").getNode("minecraft:stone").setValue("32-64");
		node.getNode("items").getNode("minecraft:dirt").setValue("32-64");
		node.getNode("items").getNode("minecraft:sand").setValue("32-64");
		node.getNode("items").getNode("minecraft:glass").setValue("32-64");
		node.getNode("items").getNode("minecraft:brick_block").setValue("32-64");
		node.getNode("items").getNode("minecraft:obsidian").setValue("0-32");
		node.getNode("items").getNode("minecraft:log").setValue("32-64");

		node = conf.getNode("lootTypes").getNode("mobs");
		node.getNode("display").setValue("{\"text\":\"Mob Loot\",\"color\":\"red\"}");
		node.getNode("items").getNode("minecraft:rotten_flesh").setValue("32-64");
		node.getNode("items").getNode("minecraft:spider_eye").setValue("0-32");
		node.getNode("items").getNode("minecraft:bone").setValue("0-32");
		node.getNode("items").getNode("minecraft:ender_pearl").setValue("0-16");
		node.getNode("items").getNode("minecraft:blaze_rod").setValue("0-32");
		node.getNode("items").getNode("minecraft:gunpowder").setValue("0-32");
		node.getNode("items").getNode("minecraft:ghast_tear").setValue("0-8");
		node.getNode("items").getNode("minecraft:slime_ball").setValue("0-16");
		node.getNode("items").getNode("minecraft:string").setValue("0-32");

		node = conf.getNode("lootTypes").getNode("goldish");
		node.getNode("display").setValue("{\"text\":\"Goldish Loot\",\"color\":\"yellow\"}");
		node.getNode("items").getNode("minecraft:gold_ingot").setValue("32-64");

		node = conf.getNode("lootTypes").getNode("food");
		node.getNode("display").setValue("{\"text\":\"Food Loot\",\"color\":\"blue\"}");
		node.getNode("items").getNode("minecraft:bread").setValue("32-128");
		node.getNode("items").getNode("minecraft:potato").setValue("8-16");
		node.getNode("items").getNode("minecraft:carrot").setValue("8-16");
		node.getNode("items").getNode("minecraft:melon").setValue("8-16");
		node.getNode("items").getNode("minecraft:pumpkin").setValue("0-32");
		node.getNode("items").getNode("minecraft:reeds").setValue("0-32");
		node.getNode("items").getNode("minecraft:fish").setValue("8-16");
		node.getNode("items").getNode("minecraft:cake").setValue("0-8");
		
		conf.getNode("worlds").getNode("world").getNode("frequency").setValue(3600);
		conf.getNode("worlds").getNode("world").getNode("minX").setValue(-5000);
		conf.getNode("worlds").getNode("world").getNode("minY").setValue(10);
		conf.getNode("worlds").getNode("world").getNode("minZ").setValue(-5000);
		conf.getNode("worlds").getNode("world").getNode("maxX").setValue(5000);
		conf.getNode("worlds").getNode("world").getNode("maxY").setValue(50);
		conf.getNode("worlds").getNode("world").getNode("maxZ").setValue(5000);
		conf.getNode("worlds").getNode("world").getNode("lootTypes").getNode("mine").setValue(15);
		conf.getNode("worlds").getNode("world").getNode("lootTypes").getNode("nether").setValue(15);
		conf.getNode("worlds").getNode("world").getNode("lootTypes").getNode("material").setValue(15);
		conf.getNode("worlds").getNode("world").getNode("lootTypes").getNode("mobs").setValue(15);
		conf.getNode("worlds").getNode("world").getNode("lootTypes").getNode("goldish").setValue(25);
		conf.getNode("worlds").getNode("world").getNode("lootTypes").getNode("food").setValue(15);
	}
	
	// LOOTCONFIG

	public static ArrayList<LootConfig> getLootConfigs()
	{
		return lootConfigs;
	}
}
