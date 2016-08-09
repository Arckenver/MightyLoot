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
import com.arckenver.mightyloot.LanguageHandler;

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
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.get("AA")));
				return CommandResult.success();
			}
		}
		
		if (!DataHandler.hasLoot(world.getName()))
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.get("AC")));
			return CommandResult.success();
		}
		
		String[] s = LanguageHandler.get("AB").split("\\{POS\\}");
		Location<World> loc = DataHandler.getLoot(world.getName()).getLoc();
		src.sendMessage(
			Text.builder()
				.append(Text.of(TextColors.GOLD, (s.length > 0) ? s[0] : ""))
				.append(Text
						.builder(loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ())
						.color(TextColors.YELLOW)
						.onClick(TextActions.runCommand("/tp " + ((player != null) ? player.getName() : "") + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ()))
						.build())
				.append(Text.of(TextColors.GOLD, (s.length > 1) ? s[1] : ""))
				.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.get("DD")))
				.build()
		);
		return CommandResult.success();
	}
}
