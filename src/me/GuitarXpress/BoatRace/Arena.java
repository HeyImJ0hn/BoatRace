package me.guitarxpress.boatrace;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.guitarxpress.boatrace.enums.Status;

public class Arena {

	private String name;
	private List<Location> spawnLocs = new ArrayList<Location>();
	private List<UUID> players = new ArrayList<UUID>();
	private List<UUID> spectators = new ArrayList<UUID>();
	private List<UUID> scoreboard = new ArrayList<UUID>();
	private Status status;
	private Location corner1;
	private Location corner2;
	private int laps;
	
	public Arena(String name, Status status, int laps) {
		this.name = name;
		this.status = status;
		this.laps = laps;
	}

	public Arena(String name, List<Location> spawnLocs, Status status, int laps, Location corner1, Location corner2) {
		this.name = name;
		this.spawnLocs = spawnLocs;
		this.status = status;
		this.laps = laps;
		this.corner1 = corner1;
		this.corner2 = corner2;
	}

	public void join(Player p) {
		players.add(p.getUniqueId());
	}

	public void leave(Player p) {
		players.remove(p.getUniqueId());
	}
	
	public void joinSpectators(Player p) {
		spectators.add(p.getUniqueId());
	}
	
	public void leaveSpectators(Player p) {
		spectators.remove(p.getUniqueId());
	}

	public List<UUID> getPlayers() {
		return players;
	}
	
	public int getPlayerCount() {
		return players.size();
	}
	
	public List<UUID> getSpectators() {
		return spectators;
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
	
	public void addSpawn(Location spawn) {
		this.spawnLocs.add(spawn);
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
