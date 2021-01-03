package me.GuitarXpress.BoatRace;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	Main plugin;
	public ArenaManager am;
	
	@Override
	public void onEnable() {
		am = new ArenaManager(this);
		getServer().getPluginManager().registerEvents(new SpawnsManager(this), this);
		getServer().getPluginManager().registerEvents(new Events(this), this);
		getServer().getPluginManager().registerEvents(new Signs(this), this);
		getServer().getPluginManager().registerEvents(new GameManager(this), this);
		getCommand("boatrace").setExecutor(new Commands(this));
		getCommand("boatrace").setTabCompleter(new TabComplete());
		
		ItemManager.init();
		
		am.load();
		System.out.println("§8[§bBoatRace§8] §aEnabled.");
	}

	@Override
	public void onDisable() {
		am.save();
		System.out.println("§8[§bBoatRace§8] §cDisabled.");
	}
	
}
