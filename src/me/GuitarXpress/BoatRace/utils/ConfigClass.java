package me.guitarxpress.boatrace.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.guitarxpress.boatrace.BoatRace;
import me.guitarxpress.boatrace.managers.GameManager;

public class ConfigClass {

	private BoatRace plugin;

	private File dataFile;
	private FileConfiguration dataCfg;

	private File arenaFolder;

	private String arenaPath = "\\Arenas";

	private Map<String, FileConfiguration> arenaConfigs = new HashMap<>();

	private Map<String, File> arenaFiles = new HashMap<>();

	private GameManager gm;

	public ConfigClass(BoatRace plugin) {
		this.plugin = plugin;

		gm = plugin.getGameManager();

		arenaFolder = new File(plugin.getDataFolder(), arenaPath);

		dataFile = new File(plugin.getDataFolder(), "data.yml");

		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage("§c[" + plugin.getName() + "] Failed to create data.yml\n" + "-> " + e);
			}
		}

		dataCfg = YamlConfiguration.loadConfiguration(dataFile);
	}

	public void createNewArenaFiles() {
		if (!arenaFolder.exists())
			arenaFolder.mkdir();

		for (String arena : gm.getArenaNames()) {
			File arenaFile = new File(arenaFolder, arena + ".yml");
			if (!arenaFile.exists())
				try {
					arenaFile.createNewFile();
				} catch (IOException e) {
					Bukkit.getServer().getConsoleSender().sendMessage(
							"§c[" + plugin.getName() + "] Failed to file for arena: " + arena + " §e-> §c" + e);
				}
			arenaFiles.put(arena, arenaFile);
		}
	}

	public void loadArenaFiles() {
		if (getArenaNameList() == null)
			return;
		for (String arena : getArenaNameList()) {
			File arenaFile = new File(arenaFolder, arena + ".yml");
			if (!arenaFile.exists())
				try {
					arenaFile.createNewFile();
				} catch (IOException e) {
					Bukkit.getServer().getConsoleSender().sendMessage(
							"§c[" + plugin.getName() + "] Failed to file for arena: " + arena + " §e-> §c" + e);
				}
			arenaFiles.put(arena, arenaFile);
		}
	}

	public File getArenaFile(String name) {
		return arenaFiles.get(name);
	}

	public FileConfiguration getArenaCfg(String name) {
		if (arenaConfigs.containsKey(name))
			return arenaConfigs.get(name);
		File arenaFile = getArenaFile(name);
		arenaConfigs.put(name, YamlConfiguration.loadConfiguration(arenaFile));
		return arenaConfigs.get(name);
	}

	public void saveArenaFile(String name) {
		File arenaFile = getArenaFile(name);
		FileConfiguration arenaCfg = getArenaCfg(name);
		try {
			arenaCfg.save(arenaFile);
		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(
					"§c[" + plugin.getName() + "] Failed to save file for arena: " + name + " §e-> §c" + e);
		}
	}

	public void deleteArena(String name) {
		File arenaFile = getArenaFile(name);
		arenaFile.delete();
	}

	public List<String> getArenaNameList() {
		String[] array = arenaFolder.list();
		if (array == null)
			return null;
		List<String> arenas = new ArrayList<>();
		for (int i = 0; i < array.length; i++) {
			arenas.add(array[i].substring(0, array[i].length() - 4));
		}
		return arenas;
	}

	public FileConfiguration getDataCfg() {
		return dataCfg;
	}

	public void saveDataCfg() {
		try {
			dataCfg.save(dataFile);
		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage("§c[GibCraft] Failed to save data.yml");
		}
	}
}
