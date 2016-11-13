package com.arckenver.mightyloot;

import java.io.File;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import com.arckenver.mightyloot.cmdelement.WorldCommandElement;
import com.arckenver.mightyloot.cmdexecutor.CancelExecutor;
import com.arckenver.mightyloot.cmdexecutor.FindExecutor;
import com.arckenver.mightyloot.cmdexecutor.HuntExecutor;
import com.arckenver.mightyloot.cmdexecutor.MightyLootExecutor;
import com.arckenver.mightyloot.cmdexecutor.ReloadExecutor;
import com.arckenver.mightyloot.cmdexecutor.SpawnExecutor;
import com.arckenver.mightyloot.listener.InteractListener;
import com.google.inject.Inject;

@Plugin(id="mightyloot", name="MightyLoot", version="2.2", authors={"Arckenver"}, description="A treasurehunt-like sponge plugin.", url="https://github.com/Arckenver/MightyLoot")
public class MightyLootPlugin
{
	private File rootDir;
	
	public static MightyLootPlugin plugin;
	
	@Inject
	private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File defaultConfFile;
	
	@Listener
	public void onStart(GameStartingServerEvent event)
	{
		logger.info("Plugin starting...");
		plugin = this;
		
		rootDir = new File(defaultConfFile.getParentFile(), "mightyloot");

		LanguageHandler.init(rootDir);
		LanguageHandler.load();
		ConfigHandler.init(rootDir);
		ConfigHandler.load();
		
		DataHandler.init();
		
		DataHandler.startSpawnTasks();
		
		CommandSpec huntCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("mightyloot.command.hunt")
				.arguments()
				.executor(new HuntExecutor())
				.build();
		
		CommandSpec findCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("mightyloot.command.find")
				.arguments(GenericArguments.optional(new WorldCommandElement(Text.of("world"))))
				.executor(new FindExecutor())
				.build();
		
		CommandSpec spawnCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("mightyloot.command.spawn")
				.arguments(GenericArguments.optional(new WorldCommandElement(Text.of("world"))))
				.executor(new SpawnExecutor())
				.build();
		
		CommandSpec cancelCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("mightyloot.command.cancel")
				.arguments(
						GenericArguments.optional(new WorldCommandElement(Text.of("world"))),
						GenericArguments.optional(GenericArguments.string(Text.of("type"))))
				.executor(new CancelExecutor())
				.build();
		
		CommandSpec reloadCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("mightyloot.command.reload")
				.arguments()
				.executor(new ReloadExecutor())
				.build();
		
		CommandSpec mightyLootCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("mightyloot.command")
				.executor(new MightyLootExecutor())
				.child(findCmd, "find")
				.child(huntCmd, "hunt", "h")
				.child(spawnCmd, "spawn")
				.child(cancelCmd, "cancel")
				.child(reloadCmd, "reload")
				.build();
		
		Sponge.getCommandManager().register(this, mightyLootCmd, "mightyloot", "ml");
		
		Sponge.getEventManager().registerListeners(this, new InteractListener());
		
		logger.info("Plugin started.");
	}
	
	@Listener
	public void onStop(GameStoppingServerEvent event)
	{
		DataHandler.removeAllLoots();
	}
	
	public static MightyLootPlugin getInstance()
	{
		return plugin;
	}

	public static Logger getLogger()
	{
		return getInstance().logger;
	}
	
	public static Cause getCause()
	{
		return Cause.source(Sponge.getPluginManager().getPlugin("mightyloot").get()).build();
	}
}
