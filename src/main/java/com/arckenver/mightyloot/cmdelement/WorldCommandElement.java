package com.arckenver.mightyloot.cmdelement;

import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

public class WorldCommandElement extends PatternMatchingCommandElement
{
	public WorldCommandElement(Text key)
	{
		super(key);
	}

	@Override
	protected Iterable<String> getChoices(CommandSource source)
	{
		return Sponge.getServer()
				.getWorlds()
				.stream()
				.map(world -> world.getName())
				.collect(Collectors.toList());
	}

	@Override
	protected Object getValue(String worldName) throws IllegalArgumentException
	{
		Optional<World> optWorld = Sponge.getServer().getWorld(worldName);
		if (optWorld.isPresent())
		{
			return optWorld.get();
		}
		throw new IllegalArgumentException("Input value " + worldName + " is an invalid world name");
	}

	public Text getUsage(CommandSource src)
	{
		return Text.EMPTY;
	}
}
