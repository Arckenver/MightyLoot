package com.arckenver.mightyloot.cmdexecutor;

import java.util.ArrayList;
import java.util.Iterator;

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
import com.flowpowered.math.vector.Vector3d;

public class HuntExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			World world = player.getWorld();
			ArrayList<Loot> loots = DataHandler.getLoots(world.getUniqueId());
			if (loots == null || loots.isEmpty())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.get("AC")));
				return CommandResult.success();
			}
			
			Iterator<Loot> iter = loots.iterator();
			Loot loot = iter.next();
			int distance = (int) player.getLocation().getPosition().distance(loot.getLoc().getPosition());
			while (iter.hasNext())
			{
				Loot otherLoot = iter.next();
				int otherDistance = (int) player.getLocation().getPosition().distance(otherLoot.getLoc().getPosition());
				if (otherDistance < distance)
				{
					loot = otherLoot;
					distance = otherDistance;
				}
			}
			
			Vector3d from = player.getLocation().getPosition();
			Vector3d to = loot.getLoc().getPosition();
			double alpha = (360 + 180 - player.getHeadRotation().getY() - (Math.atan2(from.getX() - to.getX(), from.getZ() - to.getZ())*(180/Math.PI)))%360;
			if (alpha > 180)
			{
				alpha -= 360;
			}
			alpha = (double) Math.round(alpha * 100) / 100;
			
			String direction = (alpha < 0) ? LanguageHandler.get("DC") : LanguageHandler.get("DB");
			
			src.sendMessage(Text.of(TextColors.LIGHT_PURPLE, LanguageHandler.get("DA")
					.replaceAll("\\{DEGREES\\}", String.valueOf(Math.abs(alpha)))
					.replaceAll("\\{DIRECTION\\}", direction)
					.replaceAll("\\{DISTANCE\\}", String.valueOf(distance))));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.get("AD")));
		}
		return CommandResult.success();
	}
}
