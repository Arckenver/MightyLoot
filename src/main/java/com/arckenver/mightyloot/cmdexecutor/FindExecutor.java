package com.arckenver.mightyloot.cmdexecutor;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.mightyloot.DataHandler;

public class FindExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Optional<World> optWorld = ctx.<World> getOne("world");
		World world;
		Player player = null;
		if (optWorld.isPresent())
		{
			world = optWorld.get();
		}
		else
		{
			if (src instanceof Player)
			{
				player = (Player) src;
				world = player.getWorld();
			}
			else
			{
				src.sendMessage(Text.of(TextColors.RED, "You must precise a world name to force the spawn of a loot"));
				return CommandResult.success();
			}
		}
		
		if (DataHandler.hasLoot(world.getName()))
		{
			Location<World> loc = DataHandler.getLoot(world.getName()).getLoc();
			src.sendMessage(
				Text.builder()
					.append(Text.of(TextColors.GOLD, "Loot is at position "))
					.append(Text
							.builder(loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ())
							.color(TextColors.YELLOW)
							.onClick(TextActions.runCommand("/tp " + ((player != null) ? player.getName() : "") + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ()))
							.build())
					.append(Text.of(TextColors.DARK_GRAY, " <- click"))
					.build()
			);
			return CommandResult.success();
		}
		src.sendMessage(Text.of(TextColors.RED, "Could not find any loot in this world"));
		return CommandResult.success();
	}
}
