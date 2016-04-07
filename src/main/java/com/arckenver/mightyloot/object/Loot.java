package com.arckenver.mightyloot.object;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Loot
{
	private World world;
	private Location<World> loc;
	private LootType type;
	
	public Loot(World world, Location<World> loc, LootType type)
	{
		this.world = world;
		this.loc = loc;
		this.type = type;
	}
	
	public World getWorld()
	{
		return world;
	}
	
	public Location<World> getLoc()
	{
		return loc;
	}
	
	public LootType getType()
	{
		return type;
	}
}
