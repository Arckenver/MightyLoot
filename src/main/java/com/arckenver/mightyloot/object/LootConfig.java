package com.arckenver.mightyloot.object;

import java.util.Hashtable;
import java.util.Map.Entry;

import org.apache.commons.lang3.RandomUtils;

import com.arckenver.mightyloot.area.Area;

public class LootConfig
{
	private String worldName;
	private int frequency;
	private int duration;
	private Area area;
	private Hashtable<LootType, Integer> lootTypes;
	
	public LootConfig(String worldName, int frequency, int duration, Area area)
	{
		this.worldName = worldName;
		this.frequency = frequency;
		this.duration = duration;
		this.area = area;
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

	public int getDuration()
	{
		return duration;
	}

	public Area getArea()
	{
		return area;
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
}
