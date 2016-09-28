package com.arckenver.mightyloot.area;

import java.util.concurrent.ThreadLocalRandom;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class RectArea implements Area
{
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int minZ;
	private int maxZ;
	
	public RectArea(int minX, int maxX, int minY, int maxY, int minZ, int maxZ)
	{
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.minZ = minZ;
		this.maxZ = maxZ;
	}
	
	public Location<World> getRandomLoc(World world)
	{
		int x = ThreadLocalRandom.current().nextInt(minX, maxX + 1);
		int y = ThreadLocalRandom.current().nextInt(minY, maxY + 1);
		int z = ThreadLocalRandom.current().nextInt(minZ, maxZ + 1);
		return world.getLocation(x, y, z);
	}
	
	public int getMinY()
	{
		return minY;
	}
}
