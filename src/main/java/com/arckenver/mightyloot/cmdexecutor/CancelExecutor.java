package com.arckenver.mightyloot.cmdexecutor;

import java.util.ArrayList;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.arckenver.mightyloot.DataHandler;
import com.arckenver.mightyloot.LanguageHandler;
import com.arckenver.mightyloot.object.Loot;

public class CancelExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Optional<World> optWorld = ctx.<World> getOne("world");
		World world;
		if (optWorld.isPresent())
		{
			world = optWorld.get();
		}
		else
		{
			if (src instanceof Player)
			{
				Player player = (Player) src;
				world = player.getWorld();
			}
			else
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.get("AA")));
				return CommandResult.success();
			}
		}
		ArrayList<Loot> loots = DataHandler.getLoots(world.getUniqueId());
		if (loots == null || loots.isEmpty())
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.get("AC")));
			return CommandResult.success();
		}
		Optional<String> optType = ctx.<String> getOne("type");
		if (optType.isPresent())
		{
			loots.removeIf(loot -> !loot.getType().getId().equals(optType.get()));
			if (loots.isEmpty())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.get("AG")));
				return CommandResult.success();
			}
		}
		for (Loot loot : loots)
		{
			DataHandler.removeLoot(loot);
		}
		return CommandResult.success();
	}
}
