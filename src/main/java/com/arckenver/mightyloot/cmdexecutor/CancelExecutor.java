package com.arckenver.mightyloot.cmdexecutor;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.arckenver.mightyloot.DataHandler;
import com.arckenver.mightyloot.MightyLootPlugin;
import com.arckenver.mightyloot.object.Loot;
import com.arckenver.mightyloot.object.LootConfig;

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
				src.sendMessage(Text.of(TextColors.RED, "You must precise a world name"));
				return CommandResult.success();
			}
		}
		Loot loot = DataHandler.removeLoot(world.getName());
		if (loot == null)
		{
			src.sendMessage(Text.of(TextColors.RED, "There already is no loot in this world"));
			return CommandResult.success();
		}
		for (LootConfig lootConfig : MightyLootPlugin.getInstance().getSpawnTasks().keySet())
		{
			if (lootConfig.getWorldName().equals(world.getName()))
			{
				MightyLootPlugin.getInstance().cancelSpawnTask(lootConfig);
			}
		}
		MessageChannel.TO_ALL.send(Text.builder()
				.append(Text.of(TextColors.GOLD, "The last "))
				.append(loot.getType().getDisplay())
				.append(Text.of(TextColors.GOLD, " in "))
				.append(Text.of(TextColors.YELLOW, world.getName()))
				.append(Text.of(TextColors.GOLD, " has now vanished !"))
				.build());
		return CommandResult.success();
	}
}
