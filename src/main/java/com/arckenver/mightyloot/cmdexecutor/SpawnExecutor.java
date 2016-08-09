package com.arckenver.mightyloot.cmdexecutor;

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

import com.arckenver.mightyloot.LanguageHandler;
import com.arckenver.mightyloot.MightyLootPlugin;
import com.arckenver.mightyloot.object.LootConfig;

public class SpawnExecutor implements CommandExecutor
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
		for (LootConfig lootConfig : MightyLootPlugin.getInstance().getSpawnTasks().keySet())
		{
			if (lootConfig.getWorldName().equals(world.getName()))
			{
				MightyLootPlugin.getInstance().cancelSpawnTask(lootConfig);
				MightyLootPlugin.getInstance().newSpawnTask(lootConfig);
				return CommandResult.success();
			}
		}
		src.sendMessage(Text.of(TextColors.RED, LanguageHandler.get("AF")));
		return CommandResult.success();
	}
}
