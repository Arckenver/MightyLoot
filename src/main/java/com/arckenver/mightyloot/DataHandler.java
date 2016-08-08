package com.arckenver.mightyloot;

import java.util.Hashtable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;

import com.arckenver.mightyloot.object.Loot;

public class DataHandler
{
	private static Hashtable<String, Loot> loots;
	
	public static void init()
	{
		loots = new Hashtable<String, Loot>();
	}

	// LOOT
	
	public static void setLoot(String worldName, Loot loot)
	{
		loots.put(worldName, loot);
	}
	
	public static Loot removeLoot(String worldName)
	{
		Loot loot = unregisterLoot(worldName);
		if (loot == null)
		{
			return null;
		}
		// TODO issue here : chest explodes and content stays
		/*
		((Chest) loot.getLoc().getTileEntity().get()).getInventory().clear();
		*/
		
		Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "setblock " + 
				loot.getLoc().getBlockX() + " " +
				loot.getLoc().getBlockY() + " " +
				loot.getLoc().getBlockZ() + " minecraft:chest 0 replace {Items:[]}");
		loot.getLoc().setBlockType(BlockTypes.AIR);
		loot.getLoc().add(0, -1, 0).setBlockType(BlockTypes.AIR);
		return loot;
	}
	
	public static Loot getLoot(String worldName)
	{
		return loots.get(worldName);
	}
	
	public static boolean hasLoot(String worldName)
	{
		return loots.get(worldName) != null;
	}

	public static Loot unregisterLoot(String worldName)
	{
		return loots.remove(worldName);
	}
}
