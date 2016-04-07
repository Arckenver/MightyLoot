package com.arckenver.mightyloot.object;

import java.util.Hashtable;
import java.util.Map.Entry;

import org.apache.commons.lang3.RandomUtils;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class LootConfig
{
	private String worldName;
	private int frequency;
	private int minX;
	private int minY;
	private int minZ;
	private int maxX;
	private int maxY;
	private int maxZ;
	private Hashtable<LootType, Integer> lootTypes;
	
	public LootConfig(String worldName, int frequency, int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
	{
		this.worldName = worldName;
		this.frequency = frequency;
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		this.lootTypes = new Hashtable<LootType, Integer>();
	}

	public String getWorldName()
	{
		return worldName;
	}

	public int getFrequency()
	{
		return frequency;
	}
	
	public Location<World> getRandomLoc(World world)
	{
		return world.getLocation(randomInt(minX, maxX+1), minY, randomInt(minZ, maxZ+1));
	}
	
	public int getMaxY()
	{
		return maxY;
	}
	
	public void addLootType(LootType lootType, int probability)
	{
		lootTypes.put(lootType, probability);
	}

	public Hashtable<LootType, Integer> getLootTypes()
	{
		return lootTypes;
	}
	
	public boolean isEmpty()
	{
		return lootTypes.isEmpty();
	}

	public LootType getRandomLootType()
	{
		int total = 0;
		for (int prob : lootTypes.values())
		{
			total += prob;
		}
		int random = RandomUtils.nextInt(0, total);
		for (Entry<LootType, Integer> e : lootTypes.entrySet())
		{
			random -= e.getValue();
			if (random <= 0)
			{
				return e.getKey();
			}
		}
		return null;
	}
	
	private int randomInt(int a, int b)
	{
		return (a < 0) ? RandomUtils.nextInt(0, b-a+1) + a : RandomUtils.nextInt(a, b+1);
	}
}
