package me.GuitarXpress.BoatRace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Sign;

public class ArenaManager {
	Main plugin;
	private static Location lobbyLoc;
	public static Map<String, Arena> arenas = new HashMap<String, Arena>();
	public static List<String> aName = new ArrayList<String>();

	public ArenaManager(Main plugin) {
		this.plugin = plugin;
	}

	public static void registerArena(String name, List<Location> spawns, STATUS status, int laps, Location corner1, Location corner2) {
		arenas.put(name, new Arena(spawns, status, laps, corner1, corner2));
		if (!aName.contains(name)) {
			aName.add(name);
		}
	}

	public static void setSpawns(String name, List<Location> spawns, STATUS status, int laps, Location corner1, Location corner2) {
		arenas.put(name, new Arena(spawns, status, laps, corner1, corner2));
	}

	public static void remove(String name) {
		arenas.remove(name);
		for (int i = 0; i < aName.size(); i++)
			if (aName.get(i).equals(name))
				aName.remove(i);
	}

	public static Arena getArena(String name) {
		return arenas.get(name);
	}

	public static boolean exists(String name) {
		if (arenas.get(name) != null)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public void load() {
		Signs si = new Signs(plugin);
		
		if (plugin.getConfig().get("lobbyLoc") != null) {
			setLobby(plugin.getConfig().getLocation("lobbyLoc"));
		}

		if (plugin.getConfig().get("tracks") != null) {
			aName = plugin.getConfig().getStringList("tracks.names");
			for (int i = 0; i < aName.size(); i++) {
				List<Location> spawns = (List<Location>) plugin.getConfig().getList("tracks." + aName.get(i) + ".spawns");
				STATUS status = STATUS.valueOf(plugin.getConfig().getString("tracks." + aName.get(i) + ".status"));
				int laps = plugin.getConfig().getInt("tracks." + aName.get(i) + ".laps");
				Location corner1 = plugin.getConfig().getLocation("tracks." + aName.get(i) + ".corner1");
				Location corner2 = plugin.getConfig().getLocation("tracks." + aName.get(i) + ".corner2");
				registerArena(aName.get(i), spawns, status, laps, corner1, corner2);
			}
		}
		if (plugin.getConfig().get("signs.location") != null) {
			Signs.signsLoc = (List<Location>) plugin.getConfig().getList("signs.location");
		}
		
		for (int i = 0; i < Signs.signsLoc.size(); i++) {
			Sign sign = (Sign) Signs.signsLoc.get(i).getWorld().getBlockAt(Signs.signsLoc.get(i)).getState();
			String s = sign.getLine(1);
			if (s.length() > 2) {
				s = s.substring(2, s.length()); // Remove "§6" from the line in order to get track name
			}
			int id = aName.indexOf(s);
			si.startSignUpdates(sign, s, id);
		}
		plugin.saveDefaultConfig();
		plugin.saveConfig();
	}

	public void save() {
		if (lobbyLoc != null) {
			plugin.getConfig().set("lobbyLoc", lobbyLoc);
		}
		if (!aName.isEmpty()) {
			for (int i = 0; i < aName.size(); i++) {
				plugin.getConfig().set("tracks.names", aName);
				plugin.getConfig().set("tracks." + aName.get(i) + ".spawns", arenas.get(aName.get(i)).getSpawns());
				plugin.getConfig().set("tracks." + aName.get(i) + ".status", arenas.get(aName.get(i)).getStatus().toString());
				plugin.getConfig().set("tracks." + aName.get(i) + ".laps", arenas.get(aName.get(i)).getLaps());
				plugin.getConfig().set("tracks." + aName.get(i) + ".corner1", arenas.get(aName.get(i)).getCorner1());
				plugin.getConfig().set("tracks." + aName.get(i) + ".corner2", arenas.get(aName.get(i)).getCorner2());
			}
		}
		if (!Signs.signsLoc.isEmpty()) {
			plugin.getConfig().set("signs.location", Signs.signsLoc);
		}
		plugin.saveDefaultConfig();
		plugin.saveConfig();
	}

	public static Location getLobby() {
		return lobbyLoc;
	}

	public static void setLobby(Location lobbyLocation) {
		lobbyLoc = lobbyLocation;
	}

	public static Map<String, Arena> getArenas() {
		return arenas;
	}

	public static String getArenaList() {
		String s = aName.toString();
		s = s.substring(1, s.length() - 1);
		return s;
	}

}
