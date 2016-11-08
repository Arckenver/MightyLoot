package com.arckenver.mightyloot.cmdexecutor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.mightyloot.DataHandler;
import com.arckenver.mightyloot.LanguageHandler;
import com.arckenver.mightyloot.object.Loot;

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
		ArrayList<Loot> loots = DataHandler.getLoots(world.getUniqueId());
		if (loots == null || loots.isEmpty())
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.get("AC")));
			return CommandResult.success();
		}
		
		String[] s = LanguageHandler.get("AB").split("\\{POS\\}");
		Builder builder = Text.builder();
		builder.append(Text.of(TextColors.GOLD, (s.length > 0) ? s[0] : ""));
		Iterator<Loot> iter = loots.iterator();
		while (iter.hasNext())
		{
			Location<World> loc = iter.next().getLoc();
			builder.append(Text
				.builder(loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ())
				.color(TextColors.YELLOW)
				.onClick(TextActions.runCommand("/tp " + ((player != null) ? player.getName() : "") + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ()))
				.build());
			if (iter.hasNext())
			{
				builder.append(Text.of(TextColors.GOLD, ", "));
			}
		}
		builder.append(Text.of(TextColors.GOLD, (s.length > 1) ? s[1] : ""));
		builder.append(Text.of(TextColors.DARK_GRAY, " <- " + LanguageHandler.get("DD")));
		src.sendMessage(builder.build());
		return CommandResult.success();
	}
}
