package com.arckenver.mightyloot;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import com.arckenver.mightyloot.object.Interval;
import com.arckenver.mightyloot.object.Loot;
import com.arckenver.mightyloot.object.LootConfig;
import com.arckenver.mightyloot.task.RemoveLootRunnable;
import com.arckenver.mightyloot.task.SpawnLootRunnable;

public class DataHandler
{
	private static Hashtable<UUID, ArrayList<Loot>> loots;
	private static ArrayList<Task> spawnTasks;
	
	public static void init()
	{
		loots = new Hashtable<UUID, ArrayList<Loot>>();
		spawnTasks = new ArrayList<Task>();
	}

	// LOOT
	
	public static ArrayList<Loot> getLoots(UUID worldUUID)
	{
		return loots.get(worldUUID);
	}
	
	public static void placeLoot(Loot loot)
	{
		String worldName = loot.getWorld().getName();
		Location<World> loc = loot.getLoc();
		
		if (ConfigHandler.getOptions().getNode("placeGlowstoneBelowLoot").getBoolean())
		{
			loc.setBlockType(BlockTypes.GLOWSTONE, MightyLootPlugin.getCause());
			loc = loc.add(0, 1, 0);
		}
		
		/*
		loc.setBlockType(BlockTypes.CHEST);
		Chest chest = (Chest) loc.getTileEntity().get();
		lootType.fillChest(chest.getInventory().parent());
		*/
		// TODO below we use setblock command, it shall be replaced by code using inventory api
		String setblockCmd = "setblock " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " chest 0 replace {Items:[";
		int slot = 0;
		for (Entry<ItemType, Interval> e : loot.getType().getItems().entrySet())
		{
			int amount = e.getValue().getRandom();
			int max = e.getKey().getMaxStackQuantity();
			while (amount > max)
			{
				setblockCmd += "{id:" + e.getKey().getId() + ",Count:" + max + ",Slot:" + slot + "},";
				slot++;
				amount -= max;
			}
			setblockCmd += "{id:" + e.getKey().getId() + ",Count:" + amount + ",Slot:" + slot + "},";
			slot++;
		}
		setblockCmd = setblockCmd.substring(0, setblockCmd.length() - 1) + "]}";
		Sponge.getCommandManager().process(Sponge.getServer().getConsole(), setblockCmd);
		
		loc = loc.add(0, 1, 0);
		Extent extent = loc.getExtent();
		for (Entry<EntityType, Integer> e : ConfigHandler.getLootGuardians().entrySet())
		{
			for (int i = 0; i < e.getValue(); i++)
			{
				Entity entity = extent.createEntity(e.getKey(), loc.getPosition());
				extent.spawnEntity(entity,
					Cause.source(
						EntitySpawnCause.builder().entity(entity).type(SpawnTypes.PLUGIN).build()
					).build());
			}
		}
		
		if (!loots.containsKey(loot.getWorld().getUniqueId()))
		{
			loots.put(loot.getWorld().getUniqueId(), new ArrayList<Loot>());
		}
		loots.get(loot.getWorld().getUniqueId()).add(loot);
		
		int duration = 0;
		for (LootConfig lootConfig : ConfigHandler.getLootConfigs())
		{
			if (lootConfig.getWorldName().equals(worldName))
			{
				duration = lootConfig.getDuration();
			}
		}
		
		Sponge.getScheduler()
				.createTaskBuilder()
				.execute(new RemoveLootRunnable(loot))
				.delay(duration, TimeUnit.SECONDS)
				.submit(MightyLootPlugin.getInstance());
		
		String[] s1 = LanguageHandler.get("BC").split("\\{LOOT\\}");
		String[] s2 = s1[0].split("\\{WORLD\\}");
		String[] s3 = s1[1].split("\\{WORLD\\}");
		
		String[] s = LanguageHandler.get("BD").split("\\{CMD\\}");
		
		MessageChannel.TO_ALL.send(Text.builder()
				.append(Text.of(TextColors.GOLD, (s2.length > 0) ? s2[0] : ""))
				.append(Text.of(TextColors.YELLOW, (s2.length > 1) ? worldName : ""))
				.append(Text.of(TextColors.GOLD, (s2.length > 1) ? s2[1] : ""))
				.append(loot.getType().getDisplay())
				.append(Text.of(TextColors.GOLD, (s3.length > 0) ? s3[0] : ""))
				.append(Text.of(TextColors.YELLOW, (s3.length > 1) ? worldName : ""))
				.append(Text.of(TextColors.GOLD, (s3.length > 1) ? s3[1] : ""))
				.append(Text.of(" "))
				.append(Text.of(TextColors.GOLD, (s.length > 0) ? s[0] : ""))
				.append(Text.builder("/ml hunt").color(TextColors.YELLOW).onClick(TextActions.runCommand("/mightyloot hunt")).build())
				.append(Text.of(TextColors.GOLD, (s.length > 1) ? s[1] : ""))
				.build());
	}
	
	public static boolean removeLoot(Loot loot)
	{
		ArrayList<Loot> arr = loots.get(loot.getWorld().getName());
		if (arr == null || !arr.remove(loot))
		{
			return false;
		}
		((Chest) loot.getLoc().getTileEntity().get()).getInventory().clear();
		loot.getLoc().setBlockType(BlockTypes.AIR, MightyLootPlugin.getCause());
		loot.getLoc().add(0, -1, 0).setBlockType(BlockTypes.AIR, MightyLootPlugin.getCause());
		return true;
	}
	
	public static void removeAllLoots()
	{
		for (ArrayList<Loot> arr : loots.values())
		{
			for (Loot loot : arr)
			{
				removeLoot(loot);
			}
		}
	}
	
	// TASKS
	
	public static void startSpawnTasks()
	{
		for (LootConfig lootConfig : ConfigHandler.getLootConfigs())
		{
			spawnTasks.add(Sponge.getScheduler()
					.createTaskBuilder()
					.execute(new SpawnLootRunnable(lootConfig))
					.interval(lootConfig.getFrequency(), TimeUnit.SECONDS)
					.delay(lootConfig.getFrequency(), TimeUnit.SECONDS)
					.name("MightyLoot - SpawnLoot Task - " + lootConfig.getWorldName())
					.submit(MightyLootPlugin.getInstance()));
		}
	}
	
	public static void cancelSpawnTasks()
	{
		for (Task task : spawnTasks)
		{
			task.cancel();
		}
		spawnTasks = new ArrayList<Task>();
	}
}
