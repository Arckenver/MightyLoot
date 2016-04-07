package com.arckenver.mightyloot.task;

import java.util.Map.Entry;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.mightyloot.DataHandler;
import com.arckenver.mightyloot.MightyLootPlugin;
import com.arckenver.mightyloot.object.Interval;
import com.arckenver.mightyloot.object.Loot;
import com.arckenver.mightyloot.object.LootConfig;
import com.arckenver.mightyloot.object.LootType;

public class SpawnLootRunnable implements Runnable
{
	public static final int MAX_ATTEMPTS = 200;
	
	private LootConfig lootConfig;
	
	public SpawnLootRunnable(LootConfig lootConfig)
	{
		this.lootConfig = lootConfig;
	}
	
	public void run()
	{
		LootType lootType = lootConfig.getRandomLootType();
		
		World world = Sponge.getServer().getWorld(lootConfig.getWorldName()).get();
		if (DataHandler.hasLoot(world.getName()))
		{
			DataHandler.removeLoot(world.getName());
			MessageChannel.TO_ALL.send(Text.builder()
					.append(Text.of(TextColors.GOLD, "The last "))
					.append(lootType.getDisplay())
					.append(Text.of(TextColors.GOLD, " in "))
					.append(Text.of(TextColors.YELLOW, world.getName()))
					.append(Text.of(TextColors.GOLD, " has now vanished !"))
					.build());
		}
		
		int attempt = 0;
		Location<World> loc = null;
		while (loc == null && attempt < MAX_ATTEMPTS)
		{
			attempt++;
			Location<World> randomLoc = lootConfig.getRandomLoc(world);
			BlockType lastLastBlockType = randomLoc.getBlockType();
			randomLoc.add(0, 1, 0);
			BlockType lastBlockType = randomLoc.getBlockType();
			BlockType blockType;
			while (randomLoc.getBlockY() < lootConfig.getMaxY())
			{
				randomLoc = randomLoc.add(0, 1, 0);
				blockType = randomLoc.getBlockType();
				if (lastLastBlockType.equals(BlockTypes.AIR) && lastBlockType.equals(BlockTypes.AIR) && blockType.equals(BlockTypes.AIR))
				{
					loc = randomLoc.add(0, -2, 0);
					break;
				}
				else
				{
					lastLastBlockType = lastBlockType;
					lastBlockType = blockType;
				}
			}
		}
		if (loc == null)
		{
			MightyLootPlugin.getLogger().info("After trying " + MAX_ATTEMPTS + " times, we could not find any good spawn location for the loot");
			return;
		}
		
		loc.setBlockType(BlockTypes.GLOWSTONE);
		
		loc = loc.add(0, 1, 0);
		
		// TODO below we use setblock command, it shall be replaced by code using inventory api
		/* 
		 * loc.setBlockType(BlockTypes.CHEST);
		 * Chest chest = (Chest) loc.getTileEntity().get();
		 *lootType.fillChest(chest.getInventory().parent());
		 */
		String setblockCmd = "setblock " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " chest 0 replace {Items:[";
		int slot = 0;
		for (Entry<ItemType, Interval> e : lootType.getItems().entrySet())
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
		
		DataHandler.setLoot(world.getName(), new Loot(world, loc, lootType));
		MessageChannel.TO_ALL.send(Text.builder()
				.append(Text.of(TextColors.GOLD, "A new "))
				.append(lootType.getDisplay())
				.append(Text.of(TextColors.GOLD, " has spawned in "))
				.append(Text.of(TextColors.YELLOW, world.getName()))
				.append(Text.of(TextColors.GOLD, " ! Type "))
				.append(Text.builder("/ml hunt").color(TextColors.YELLOW).onClick(TextActions.runCommand("/mightyloot hunt")).build())
				.build());
	}
}
