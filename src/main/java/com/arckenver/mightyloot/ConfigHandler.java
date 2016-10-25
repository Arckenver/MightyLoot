package com.arckenver.mightyloot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.ItemType;

import com.arckenver.mightyloot.area.Area;
import com.arckenver.mightyloot.area.CylArea;
import com.arckenver.mightyloot.area.RectArea;
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
	private static Hashtable<EntityType, Integer> lootGuardians;
	
	public static void init(File rootDir)
	{
		configFile = new File(rootDir, "config.conf");
		configManager = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();
	}
	
	public static void load()
	{
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
		
		ensureBoolean(config.getNode("options", "placeGlowstoneBelowLoot"), true);
		ensureBoolean(config.getNode("options", "placeAlwaysOnGround"), true);
		
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
				Area area = null;
				String type = worldNode.getNode("area", "type").getString("no value");
				if (type.equals("rect"))
				{
					area = new RectArea(
							worldNode.getNode("area", "minX").getInt(),
							worldNode.getNode("area", "maxX").getInt(),
							worldNode.getNode("area", "minY").getInt(),
							worldNode.getNode("area", "maxY").getInt(),
							worldNode.getNode("area", "minZ").getInt(),
							worldNode.getNode("area", "maxZ").getInt()
					);
				}
				else if (type.equals("cyl"))
				{
					area = new CylArea(
							worldNode.getNode("area", "centerX").getInt(),
							worldNode.getNode("area", "centerZ").getInt(),
							worldNode.getNode("area", "radius").getInt(),
							worldNode.getNode("area", "minY").getInt(),
							worldNode.getNode("area", "maxY").getInt()
					);
				}
				else
				{
					MightyLootPlugin.getLogger().error("Error while loading config file: " + type + " is not a valid area type.");
					continue;
				}
				LootConfig lootConfig = new LootConfig(
						worldName, 
						worldNode.getNode("frequency").getInt(),
						worldNode.getNode("duration").getInt(),
						area
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
		lootGuardians = new Hashtable<EntityType, Integer>();
		for (Entry<Object, ? extends CommentedConfigurationNode> entry : config.getNode("options", "lootGuardians").getChildrenMap().entrySet())
		{
			Optional<EntityType> optEntityType = Sponge.getRegistry().getType(EntityType.class, entry.getKey().toString());
			if (optEntityType.isPresent())
			{
				lootGuardians.put(optEntityType.get(), entry.getValue().getInt());
			}
			else
			{
				MightyLootPlugin.getLogger().error("Error while reading loot guardians in config: " + entry.getKey().toString() + " is not an entity type");
			}
		}
		saveConfig();
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
		
		node = conf.getNode("lootTypes", "mine");
		node.getNode("display").setValue("{\"text\":\"Mine Loot\",\"color\":\"aqua\"}");
		node.getNode("items", "minecraft:emerald").setValue("1-6");
		node.getNode("items", "minecraft:diamond").setValue("2-8");
		node.getNode("items", "minecraft:gold_ingot").setValue("5-16");
		node.getNode("items", "minecraft:iron_ingot").setValue("16-64");
		node.getNode("items", "minecraft:coal").setValue("16-64");
		node.getNode("items", "minecraft:redstone").setValue("0-128");
		node.getNode("items", "minecraft:flint").setValue("0-32");
		node.getNode("items", "minecraft:stone").setValue("32-128");
		node.getNode("items", "minecraft:dirt").setValue("0-64");
		node.getNode("items", "minecraft:gravel").setValue("0-32");
		
		node = conf.getNode("lootTypes", "nether");
		node.getNode("display").setValue("{\"text\":\"Nether Loot\",\"color\":\"dark_purple\"}");
		node.getNode("items", "minecraft:netherrack").setValue("64-128");
		node.getNode("items", "minecraft:nether_brick").setValue("32-128");
		node.getNode("items", "minecraft:nether_wart").setValue("0-32");
		node.getNode("items", "minecraft:soul_sand").setValue("0-64");
		node.getNode("items", "minecraft:glowstone").setValue("16-64");
		node.getNode("items", "minecraft:quartz").setValue("64-256");

		node = conf.getNode("lootTypes", "material");
		node.getNode("display").setValue("{\"text\":\"Material Loot\",\"color\":\"gray\"}");
		node.getNode("items", "minecraft:stone").setValue("32-64");
		node.getNode("items", "minecraft:dirt").setValue("32-64");
		node.getNode("items", "minecraft:sand").setValue("32-64");
		node.getNode("items", "minecraft:glass").setValue("32-64");
		node.getNode("items", "minecraft:brick_block").setValue("32-64");
		node.getNode("items", "minecraft:obsidian").setValue("0-32");
		node.getNode("items", "minecraft:log").setValue("32-64");

		node = conf.getNode("lootTypes", "mobs");
		node.getNode("display").setValue("{\"text\":\"Mob Loot\",\"color\":\"red\"}");
		node.getNode("items", "minecraft:rotten_flesh").setValue("32-64");
		node.getNode("items", "minecraft:spider_eye").setValue("0-32");
		node.getNode("items", "minecraft:bone").setValue("0-32");
		node.getNode("items", "minecraft:ender_pearl").setValue("0-16");
		node.getNode("items", "minecraft:blaze_rod").setValue("0-32");
		node.getNode("items", "minecraft:gunpowder").setValue("0-32");
		node.getNode("items", "minecraft:ghast_tear").setValue("0-8");
		node.getNode("items", "minecraft:slime_ball").setValue("0-16");
		node.getNode("items", "minecraft:string").setValue("0-32");

		node = conf.getNode("lootTypes", "goldish");
		node.getNode("display").setValue("{\"text\":\"Goldish Loot\",\"color\":\"yellow\"}");
		node.getNode("items", "minecraft:gold_ingot").setValue("32-64");

		node = conf.getNode("lootTypes", "food");
		node.getNode("display").setValue("{\"text\":\"Food Loot\",\"color\":\"blue\"}");
		node.getNode("items", "minecraft:bread").setValue("32-128");
		node.getNode("items", "minecraft:potato").setValue("8-16");
		node.getNode("items", "minecraft:carrot").setValue("8-16");
		node.getNode("items", "minecraft:melon").setValue("8-16");
		node.getNode("items", "minecraft:pumpkin").setValue("0-32");
		node.getNode("items", "minecraft:reeds").setValue("0-32");
		node.getNode("items", "minecraft:fish").setValue("8-16");
		node.getNode("items", "minecraft:cake").setValue("0-8");
		
		conf.getNode("worlds", "world", "frequency").setValue(3600);
		conf.getNode("worlds", "world", "duration").setValue(4600);
		conf.getNode("worlds", "world", "area").setComment("represents the area where the loot can spawn\nfor 'type', accepted values are 'rect' and 'cyl'\nif using 'rect', specify 'minX', 'maxX', 'minY', 'maxY', 'minZ', 'maxZ'\nif using 'cyl', specify 'minY', 'maxY', 'centerX', 'centerZ', 'radius'");
		conf.getNode("worlds", "world", "area", "type").setValue("rect");
		conf.getNode("worlds", "world", "area", "minX").setValue(-5000);
		conf.getNode("worlds", "world", "area", "minY").setValue(10);
		conf.getNode("worlds", "world", "area", "minZ").setValue(-5000);
		conf.getNode("worlds", "world", "area", "maxX").setValue(5000);
		conf.getNode("worlds", "world", "area", "maxY").setValue(50);
		conf.getNode("worlds", "world", "area", "maxZ").setValue(5000);
		conf.getNode("worlds", "world", "lootTypes", "mine").setValue(15);
		conf.getNode("worlds", "world", "lootTypes", "nether").setValue(15);
		conf.getNode("worlds", "world", "lootTypes", "material").setValue(15);
		conf.getNode("worlds", "world", "lootTypes", "mobs").setValue(15);
		conf.getNode("worlds", "world", "lootTypes", "goldish").setValue(25);
		conf.getNode("worlds", "world", "lootTypes", "food").setValue(15);
		
		conf.getNode("options", "lootGuardians", "Zombie").setValue(0).setComment("the plugin will make those mobs spawn with the loot\nspecify mob name using EntityType (Zombie, Skeleton, Spider etc) and number of entity to make spawn");
	}

	public static ArrayList<LootConfig> getLootConfigs()
	{
		return lootConfigs;
	}

	public static Hashtable<EntityType, Integer> getLootGuardians()
	{
		return lootGuardians;
	}
	
	public static CommentedConfigurationNode getOptions()
	{
		return config.getNode("options");
	}
	
	/*
	private static void ensureString(CommentedConfigurationNode node, String def)
	{
		if (node.getString() == null)
		{
			node.setValue(def);
		}
	}

	private static void ensurePositiveNumber(CommentedConfigurationNode node, Number def)
	{
		if (!(node.getValue() instanceof Number) || node.getDouble(-1) < 0)
		{
			node.setValue(def);
		}
	}
	*/
	
	private static void ensureBoolean(CommentedConfigurationNode node, boolean def)
	{
		if (!(node.getValue() instanceof Boolean))
		{
			node.setValue(def);
		}
	}
}
