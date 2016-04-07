package com.arckenver.mightyloot.cmdexecutor;

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
			if (!DataHandler.hasLoot(world.getName()))
			{
				src.sendMessage(Text.of(TextColors.RED, "There is currently no loot in this world"));
				return CommandResult.success();
			}
			Loot loot = DataHandler.getLoot(world.getName());
			int distance = (int) player.getLocation().getPosition().distance(loot.getLoc().getPosition());
			
			Vector3d from = player.getLocation().getPosition();
			Vector3d to = loot.getLoc().getPosition();
			double alpha = (360 + 180 - player.getHeadRotation().getY() - (Math.atan2(from.getX() - to.getX(), from.getZ() - to.getZ())*(180/Math.PI)))%360;
			if (alpha > 180)
			{
				alpha -= 360;
			}
			alpha = (double) Math.round(alpha * 100) / 100;
			
			src.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "You must turn " + ((alpha < 0) ? -alpha + "° to the left" : alpha + "° to the right") + " and the chest will be at a distance of " + distance + " blocks"));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, "You must be an in game player to perform that command"));
		}
		return CommandResult.success();
	}
}
