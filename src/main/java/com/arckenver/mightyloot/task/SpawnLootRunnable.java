package com.arckenver.mightyloot.task;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.mightyloot.ConfigHandler;
import com.arckenver.mightyloot.DataHandler;
import com.arckenver.mightyloot.MightyLootPlugin;
import com.arckenver.mightyloot.object.Loot;
import com.arckenver.mightyloot.object.LootConfig;
import com.arckenver.mightyloot.object.LootType;

public class SpawnLootRunnable implements Runnable
{
	public static final int MAX_ATTEMPTS = 1000;
	
	private LootConfig lootConfig;
	
	public SpawnLootRunnable(LootConfig lootConfig)
	{
		this.lootConfig = lootConfig;
	}
	
	public void run()
	{
		LootType lootType = lootConfig.getRandomLootType();
		
		World world = Sponge.getServer().getWorld(lootConfig.getWorldName()).get();
		
		int attempt = 0;
		Location<World> loc = null;
		while (loc == null && attempt < MAX_ATTEMPTS)
		{
			attempt++;
			Location<World> randomLoc = lootConfig.getArea().getRandomLoc(world);
			if (!randomLoc.getBlockType().equals(BlockTypes.AIR) || 
					!randomLoc.add(0, 1, 0).getBlockType().equals(BlockTypes.AIR))
			{
				continue;
			}
			if (ConfigHandler.getOptions().getNode("placeGlowstoneBelowLoot").getBoolean())
			{
				randomLoc = randomLoc.add(0, -1, 0);
				if (!randomLoc.getBlockType().equals(BlockTypes.AIR))
				{
					continue;
				}
			}
			if (ConfigHandler.getOptions().getNode("placeAlwaysOnGround").getBoolean())
			{
				while (randomLoc.add(0, -1, 0).getBlockType().equals(BlockTypes.AIR) && randomLoc.getBlockY() >= lootConfig.getArea().getMinY())
				{
					randomLoc = randomLoc.add(0, -1, 0);
				}
				if (randomLoc.add(0, -1, 0).getBlockType().equals(BlockTypes.AIR))
				{
					continue;
				}
			}
			loc = randomLoc;
		}
		if (loc == null)
		{
			MightyLootPlugin.getLogger().info("After trying " + MAX_ATTEMPTS + " times, we could not find any good spawn location for the loot");
			return;
		}
		
		DataHandler.placeLoot(new Loot(world, loc, lootType));
	}
}
