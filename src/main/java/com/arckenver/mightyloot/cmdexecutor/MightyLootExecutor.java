package com.arckenver.mightyloot.cmdexecutor;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class MightyLootExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Text.Builder builder = Text.builder()
				.append(Text.of(TextColors.GOLD, "--------{ ", TextColors.YELLOW, "/mightyloot", TextColors.GOLD, " }--------"));
		
		if (src.hasPermission("mightyloot.command.hunt"))
		{
			builder.append(Text.of(TextColors.GOLD, "\n/ml hunt", TextColors.GRAY, " - ", TextColors.YELLOW, "gives you your distance to the loot in this world"));
		}
		if (src.hasPermission("mightyloot.command.find"))
		{
			builder.append(Text.of(TextColors.GOLD, "\n/ml find", TextColors.GRAY, " - ", TextColors.YELLOW, "gives chest location"));
		}
		if (src.hasPermission("mightyloot.command.spawn"))
		{
			builder.append(Text.of(TextColors.GOLD, "\n/ml spawn [world]", TextColors.GRAY, " - ", TextColors.YELLOW, "forces the spawn of a new loot"));
		}
		if (src.hasPermission("mightyloot.command.cancel"))
		{
			builder.append(Text.of(TextColors.GOLD, "\n/ml cancel [world]", TextColors.GRAY, " - ", TextColors.YELLOW, "removes the loot from a world"));
		}
		if (src.hasPermission("mightyloot.command.reload"))
		{
			builder.append(Text.of(TextColors.GOLD, "\n/ml reload", TextColors.GRAY, " - ", TextColors.YELLOW, "reloads the config file"));
		}
		
		src.sendMessage(builder.build());
		return CommandResult.success();
	}
}
