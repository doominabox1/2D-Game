package unused;

import interfaces.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import ships.Ship;
import ships.ShipPart;
import util.SpatialHash;
import util.Utility;

public class Buoy  extends Ship implements Renderable{

	public Buoy(int xSize, int ySize, String spriteSheetPath, int spriteSize) throws SlickException {
		super(1, 1, spriteSheetPath, spriteSize);
		ShipPart sp = new ShipPart(ShipPart.TIER1, ShipPart.NOTHING, ShipPart.NOTHING, ShipPart.NOTHING);
		this.setPart(sp, 0, 0);
		updateBlocks();
		velocity.set(0.1f, 0);
	}

	@Override
	public void render(GameContainer gc, Graphics g, Rectangle camera) {
		if(positionOnScreen == null){
			positionOnScreen = new Vector2f(gc.getWidth() / 2, gc.getHeight() / 2);
		}
		Vector2f drawPosition = new Vector2f(position);
		drawPosition.sub(camera.getLocation());
		for (int i = 0; i < ship.length; i++) {			// Draw each ship component
			for (int j = 0; j < ship[0].length; j++) {
				Image curImage = hullList[i][j];
				if(curImage == null){
					continue;
				}
				Vector2f p = Utility.getVectorFromArrayLocation(i, j, shipSize, ship);
				curImage.setCenterOfRotation(center.x - p.x, center.y - p.y);
				curImage.setRotation((float) angle);
				curImage.draw(drawPosition.x - center.x + p.x, drawPosition.y - center.y + p.y, (float) shipSize, (float) shipSize);
			}
		}
	}
	
	@Override
	public void update(int delta, Input input, SpatialHash sh){
		Vector2f temp = new Vector2f(velocity);
		temp.scale(delta);
		position.add(temp);
		
		sh.update(this, temp);
		//velocity.set(0, 0);
	}
}
