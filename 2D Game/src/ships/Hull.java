package ships;

import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;

import def.ShipPart;
import def.Utility;

public class Hull {
	private ShipPart[][] shipPart;
	private Polygon hull;
	private Ship ship;
	public Hull(ShipPart[][] shipParts, Ship s){
		shipPart = shipParts;
		ship = s;
		update();
	}
	public void update(){	// Please someone make a better function
		boolean[][] ship = new boolean[shipPart.length][shipPart[0].length];
		hull = new Polygon();
		for (int i = 0; i < ship.length; i++) {
			for (int j = 0; j < ship[0].length; j++) {
				ship[i][j] = shipPart[i][j].getHullTier() != ShipPart.NOTHING;
			}
		}
		int curX = 0;
		int curY = 0;
		
		outer:
		for(curY = 0; curY < ship[0].length; curY++){
			for(curX = 0; curX < ship.length; curX++){
				if(ship[curX][curY]){
					break outer;
				}
			}
		}
		
		Vector2f loc = Utility.getVectorFromArrayLocation(curX, curY, this.ship.getSize(), shipPart);
		hull.addPoint(loc.x - this.ship.getCenterObject().x, loc.y - this.ship.getCenterObject().y);
		
		int xb = curX;
		int yb = curY;
		
		int oldX = curX;
		int oldY = curY;
		curX++;
		
		while(!(curX == xb && curY == yb)){
			
			loc = Utility.getVectorFromArrayLocation(curX, curY, this.ship.getSize(), shipPart);
			
			hull.addPoint(loc.x - this.ship.getCenterObject().x, loc.y - this.ship.getCenterObject().y);
			
			String s = "";
			for(int y = -1; y <= 0; y++){
				for(int x = -1; x <= 0; x++){
					try{
						if(ship[curX + x][curY + y]){
							s += "1";
						}else{
							s += "0";
						}
					}catch(IndexOutOfBoundsException e){
						s += "0";
					}
				}
			}
			
			if(s.equals("0111")){		// 1
				if(oldX == curX - 1 && oldY == curY){
					oldX = curX;
					oldY = curY;
					curY--;
				}else{
					oldX = curX;
					oldY = curY;
					curX--;
				}
			}else if(s.equals("1011")){	// 2
				if(oldX == curX && oldY == curY - 1){
					oldX = curX;
					oldY = curY;
					curX++;
				}else{
					oldX = curX;
					oldY = curY;
					curY--;
				}
			}else if(s.equals("1101")){	// 3
				if(oldX == curX - 1 && oldY == curY){
					oldX = curX;
					oldY = curY;
					curY++;
				}else{
					oldX = curX;
					oldY = curY;
					curX--;
				}
			}else if(s.equals("1110")){	// 4
				if(oldX == curX && oldY == curY + 1){
					oldX = curX;
					oldY = curY;
					curX++;
				}else{
					oldX = curX;
					oldY = curY;
					curY++;
				}
			}else if(s.equals("1010")){	// 5
				if(oldX == curX && oldY == curY - 1){
					oldX = curX;
					oldY = curY;
					curY++;
				}else{
					oldX = curX;
					oldY = curY;
					curY--;
				}
			}else if(s.equals("1100")){	// 6
				if(oldX == curX - 1 && oldY == curY){
					oldX = curX;
					oldY = curY;
					curX++;
				}else{
					oldX = curX;
					oldY = curY;
					curX--;
				}
			}else if(s.equals("0011")){	// 7
				if(oldX == curX - 1 && oldY == curY){
					oldX = curX;
					oldY = curY;
					curX++;
				}else{
					oldX = curX;
					oldY = curY;
					curX--;
				}
			}else if(s.equals("0101")){	// 8
				if(oldX == curX && oldY == curY - 1){
					oldX = curX;
					oldY = curY;
					curY++;
				}else{
					oldX = curX;
					oldY = curY;
					curY--;
				}
			}else if(s.equals("1000")){	// 9
				if(oldX == curX - 1&& oldY == curY){
					oldX = curX;
					oldY = curY;
					curY--;
				}else{
					oldX = curX;
					oldY = curY;
					curX--;
				}
			}else if(s.equals("0100")){	// 10
				if(oldX == curX + 1 && oldY == curY){
					oldX = curX;
					oldY = curY;
					curY--;
				}else{
					oldX = curX;
					oldY = curY;
					curX++;
				}
			}else if(s.equals("0010")){	// 11
				if(oldX == curX - 1 && oldY == curY){
					oldX = curX;
					oldY = curY;
					curY++;
				}else{
					oldX = curX;
					oldY = curY;
					curX--;
				}
			}else if(s.equals("0001")){	// 12
				if(oldX == curX + 1 && oldY == curY){
					oldX = curX;
					oldY = curY;
					curY++;
				}else{
					oldX = curX;
					oldY = curY;
					curX++;
				}
			}else{
				throw new IndexOutOfBoundsException();
			}
		}
	}
	
	public Polygon getHull(){
		if(hull == null){
			update();
		}
		return hull.copy();
	}
	
}
