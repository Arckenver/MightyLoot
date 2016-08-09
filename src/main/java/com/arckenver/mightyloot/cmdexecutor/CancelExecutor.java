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
import com.arckenver.mightyloot.LanguageHandler;
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
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.get("AA")));
				return CommandResult.success();
			}
		}
		Loot loot = DataHandler.removeLoot(world.getName());
		if (loot == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.get("AC")));
			return CommandResult.success();
		}
		for (LootConfig lootConfig : MightyLootPlugin.getInstance().getSpawnTasks().keySet())
		{
			if (lootConfig.getWorldName().equals(world.getName()))
			{
				MightyLootPlugin.getInstance().cancelSpawnTask(lootConfig);
			}
		}
		
		String[] s1 = LanguageHandler.get("BB").split("\\{LOOT\\}");
		String[] s2 = s1[0].split("\\{WORLD\\}");
		String[] s3 = s1[1].split("\\{WORLD\\}");
		
		MessageChannel.TO_ALL.send(Text.builder()
				.append(Text.of(TextColors.GOLD, (s2.length > 0) ? s2[0] : ""))
				.append(Text.of(TextColors.YELLOW, world.getName()))
				.append(Text.of(TextColors.GOLD, (s2.length > 1) ? s2[1] : ""))
				.append(loot.getType().getDisplay())
				.append(Text.of(TextColors.GOLD, (s3.length > 0) ? s3[0] : ""))
				.append(Text.of(TextColors.YELLOW, world.getName()))
				.append(Text.of(TextColors.GOLD, (s3.length > 1) ? s3[1] : ""))
				.build());
		return CommandResult.success();
	}
}
