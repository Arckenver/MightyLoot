package com.arckenver.mightyloot;

import java.util.Hashtable;

import org.spongepowered.api.block.BlockTypes;

import com.arckenver.mightyloot.object.Loot;
import org.spongepowered.api.block.tileentity.carrier.Chest;

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
		((Chest) loot.getLoc().getTileEntity().get()).getInventory().clear();
		loot.getLoc().setBlockType(BlockTypes.AIR, MightyLootPlugin.getCause());
		loot.getLoc().add(0, -1, 0).setBlockType(BlockTypes.AIR, MightyLootPlugin.getCause());
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
