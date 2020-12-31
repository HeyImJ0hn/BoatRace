package me.GuitarXpress.BoatRace;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Arena {

	private List<Location> spawnLocs = new ArrayList<Location>();

	private List<UUID> players = new ArrayList<UUID>();
	
	private List<UUID> scoreboard = new ArrayList<UUID>();
	
	STATUS status;
	
	Location corner1;
	Location corner2;
	
	int laps;

	public Arena(List<Location> spawnLocs, STATUS status, int laps, Location corner1, Location corner2) {
		this.spawnLocs = spawnLocs;
		this.status = status;
		this.laps = laps;
		this.corner1 = corner1;
		this.corner2 = corner2;
	}

	public void join(Player p, String s) {
		players.add(p.getUniqueId());
		Commands.pArena.put(p.getUniqueId(), s);
	}

	public void leave(Player p) {
		players.remove(p.getUniqueId());
		Commands.pArena.put(p.getUniqueId(), null);
	}

	public List<UUID> getPlayers() {
		return players;
	}
	
	public List<UUID> getScoreboard(){
		return scoreboard;
	}
	
	public void addScore(Player p) {
		scoreboard.add(p.getUniqueId());
	}
	
	public void clearScore() {
		scoreboard = new ArrayList<UUID>();
	}

	public List<Location> getSpawns() {
		return spawnLocs;
	}

	public void setSpawns(List<Location> spawnLocations) {
		this.spawnLocs = spawnLocations;
	}
	
	public STATUS getStatus() {
		return status;
	}
	
	public void setStatus(STATUS status) {
		this.status = status;
	}
	
	public int getLaps() {
		return this.laps;
	}
	
	public void setLaps(int laps) {
		this.laps = laps;
	}

	public Location getCorner1() {
		return corner1;
	}

	public void setCorner1(Location corner1) {
		this.corner1 = corner1;
	}

	public Location getCorner2() {
		return corner2;
	}

	public void setCorner2(Location corner2) {
		this.corner2 = corner2;
	}
	
}
