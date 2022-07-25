package me.guitarxpress.boatrace;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.guitarxpress.boatrace.enums.Status;
import me.guitarxpress.boatrace.events.EditMode;
import me.guitarxpress.boatrace.events.PlayerInteract;
import me.guitarxpress.boatrace.events.PlayerInteractEntity;
import me.guitarxpress.boatrace.events.PlayerMove;
import me.guitarxpress.boatrace.events.PlayerQuit;
import me.guitarxpress.boatrace.events.SignEvents;
import me.guitarxpress.boatrace.events.VehicleExit;
import me.guitarxpress.boatrace.managers.GameManager;
import me.guitarxpress.boatrace.managers.ItemManager;
import me.guitarxpress.boatrace.utils.ConfigClass;
import me.guitarxpress.boatrace.utils.RepeatingTask;
import me.guitarxpress.boatrace.utils.Utils;

public class BoatRace extends JavaPlugin {

	private GameManager gm;
	private ConfigClass cfg;
	private FileConfiguration dataCfg;
	
	public ConfigClass getCfg() {
		return cfg;
	}

	@Override
	public void onEnable() {
		ItemManager.init();
		gm = new GameManager(this);
		cfg = new ConfigClass(this);
		dataCfg = cfg.getDataCfg();

		getConfig().options().copyDefaults(true);
		saveDefaultConfig();

		getServer().getPluginManager().registerEvents(new EditMode(this), this);
		getServer().getPluginManager().registerEvents(new SignEvents(this), this);
		getServer().getPluginManager().registerEvents(new PlayerInteract(this), this);
		getServer().getPluginManager().registerEvents(new PlayerInteractEntity(this), this);
		getServer().getPluginManager().registerEvents(new PlayerMove(this), this);
		getServer().getPluginManager().registerEvents(new PlayerQuit(this), this);
		getServer().getPluginManager().registerEvents(new VehicleExit(this), this);
		getCommand("boatrace").setExecutor(new Commands(this));
		getCommand("boatrace").setTabCompleter(new TabComplete(this));
		load();
		getServer().getConsoleSender().sendMessage("§8[§bBoatRace§8] §aEnabled.");
		startTickRunnable();
	}

	@Override
	public void onDisable() {
		save();
		getServer().getConsoleSender().sendMessage("§8[§bBoatRace§8] §cDisabled.");
	}

	public GameManager getGameManager() {
		return gm;
	}

	@SuppressWarnings("unchecked")
	public void load() {
		if (dataCfg.get("LobbyLoc") != null)
			gm.setLobby(dataCfg.getLocation("LobbyLoc"));
		
		if (dataCfg.get("SignsLoc") != null)
			SignEvents.signsLoc = (List<Location>) dataCfg.getList("SignsLoc");

		loadArenas();

		if (getConfig().get("variables") != null) {
			gm.setWaitTime(getConfig().getInt("variables.waittime"));
		} else {
			getServer().getConsoleSender()
					.sendMessage("§8[§bBoatRace§8] " + "§cCould not get variables from config - Empty");
		}
	}

	public void save() {
		if (gm.getLobby() != null)
			dataCfg.set("LobbyLoc", gm.getLobby());

		if (!SignEvents.signsLoc.isEmpty())
			dataCfg.set("SignsLoc", SignEvents.signsLoc);

		if (!gm.getArenaNames().isEmpty())
			for (Arena arena : gm.getArenas())
				saveArena(arena);
		cfg.saveDataCfg();
	}

	public void saveArena(Arena arena) {
		cfg.createNewArenaFiles();
		FileConfiguration aCfg = cfg.getArenaCfg(arena.getName());
		aCfg.set("Name", arena.getName());
		aCfg.set("Spawns", arena.getSpawns());
		aCfg.set("Status", arena.getStatus().toString());
		aCfg.set("Laps", arena.getLaps());
		aCfg.set("Corner1", arena.getCorner1());
		aCfg.set("Corner2", arena.getCorner2());
		cfg.saveArenaFile(arena.getName());
	}
	
	@SuppressWarnings("unchecked")
	public void loadArenas() {
		cfg.loadArenaFiles();
		List<String> arenas = cfg.getArenaNameList();
		if (arenas == null)
			return;
		for (String arena : arenas) {
			FileConfiguration aCfg = cfg.getArenaCfg(arena);
			String name = aCfg.getString("Name");
			List<Location> spawns = (List<Location>) aCfg.getList("Spawns");
			Status status = Status.valueOf(aCfg.getString("Status"));
			int laps = aCfg.getInt("Laps");
			Location corner1 = aCfg.getLocation("Corner1");
			Location corner2 = aCfg.getLocation("Corner2");
			gm.registerArena(name, spawns, status, laps, corner1, corner2);
		}
	}

	public void startTickRunnable() {
		new RepeatingTask(this, 0, 1) {

			@Override
			public void run() {
				Sign toRemove = null;
				for (Location loc : SignEvents.signsLoc) {
					Sign sign = (Sign) loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())
							.getState();
					String s = Utils.getNameFromString(sign.getLine(1));
					if (!gm.exists(s)) {
						toRemove = sign;
						sign.getBlock().breakNaturally();
					} else {
						SignEvents.updateSign(sign, gm, s);
					}
				}
				if (toRemove != null)
					SignEvents.signsLoc.remove(toRemove.getLocation());
			}

		};
	}

}
