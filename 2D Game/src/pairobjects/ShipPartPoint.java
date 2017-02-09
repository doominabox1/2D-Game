package pairobjects;

import java.awt.Point;

import ships.ShipPart;

public class ShipPartPoint {	// Pair object for a physical point in space and a part object
	public ShipPart shipPart;
	public Point point;
	public ShipPartPoint(ShipPart sp, Point p){
		shipPart = sp;
		point = p;
	}
}
