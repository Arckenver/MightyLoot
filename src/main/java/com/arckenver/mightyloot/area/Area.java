package com.arckenver.mightyloot.area;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public interface Area
{
	public Location<World> getRandomLoc(World world);
	public int getMaxY();
}
