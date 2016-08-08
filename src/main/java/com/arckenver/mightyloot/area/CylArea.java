package com.arckenver.mightyloot.area;

import org.apache.commons.lang3.RandomUtils;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CylArea implements Area
{
	private int centerX;
	private int centerZ;
	private int radius;
	private int minY;
	private int maxY;
	
	public CylArea(int centerX, int centerZ, int radius, int minY, int maxY)
	{
		this.centerX = centerX;
		this.centerZ = centerZ;
		this.radius = radius;
		this.minY = minY;
		this.maxY = maxY;
	}

	public Location<World> getRandomLoc(World world)
	{
		int x;
		int z;
		do
		{
			x = RandomUtils.nextInt(centerX - radius, centerX + radius);
			z = RandomUtils.nextInt(centerZ - radius, centerZ + radius);
		}
		while(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2) > Math.pow(radius, 2));
		
		int y = RandomUtils.nextInt(minY, maxY + 1);
		return world.getLocation(x, y, z);
	}
	
	public int getMaxY()
	{
		return maxY;
	}
}
