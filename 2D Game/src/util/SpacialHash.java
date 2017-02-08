package util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.geom.Line;

import ships.Ship;

public class SpacialHash {
	private int bucketSize;
	HashMap<Point, ArrayList<Ship>> ships;
	
	public SpacialHash(int bucketSize){
		this.bucketSize = bucketSize;
		ships = new HashMap<Point, ArrayList<Ship>>();
	}
	public void remove(Ship ship){
		// TODO get the basic hull, get each corner (subpoints if larger than bucket), remove ship from each bucket
	}
	public void add(Ship ship){
		// TODO get the basic hull, get each corner (subpoints if larger than bucket), add ship to each bucket
	}
	private Point hash(Ship ship){
		return new Point((int) (ship.getX() / bucketSize), (int) (ship.getY() / bucketSize));
	}
	public static boolean lineHitDetect(Ship source, Line line){  // Use lineDetect to get ships to scan, then find the closest ship that it hits
		// TODO fix
		// return destination.getSimpleClippingHull().intersects(line) && destination.getClippingHull().intersects(line);
		return false;
	}
	private ArrayList<Ship> getShipsNearLine(Ship ship, Line line){ // Gets a list of all ships that intersect `line` besides `ship` 
		// TODO https://www.gamedev.net/resources/_/technical/game-programming/spatial-hashing-r2697
		return null;
	}
}
