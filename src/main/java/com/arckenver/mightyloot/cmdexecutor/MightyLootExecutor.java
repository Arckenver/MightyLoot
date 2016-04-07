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
		src.sendMessage(Text
				.builder()
				.append(Text.of(TextColors.GOLD, "--------{ ", TextColors.YELLOW, "/mightyloot", TextColors.GOLD, " }--------\n"))
				.append(Text.of(TextColors.GOLD, "/ml hunt", TextColors.GRAY, " - ", TextColors.YELLOW, "gives you your distance to the loot in this world\n"))
				.append(Text.of(TextColors.GOLD, "/ml find", TextColors.GRAY, " - ", TextColors.YELLOW, "(admin) gives chest location\n"))
				.append(Text.of(TextColors.GOLD, "/ml spawn [world]", TextColors.GRAY, " - ", TextColors.YELLOW, "(admin) forces the spawn of a new loot\n"))
				.append(Text.of(TextColors.GOLD, "/ml cancel [world]", TextColors.GRAY, " - ", TextColors.YELLOW, "(admin) removes the loot from a world"))
				.build()
		);
		return CommandResult.success();
	}
}
