package ships;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import def.Renderable;
import def.Utility;


public class PlayerShip extends Ship implements Renderable{
	public PlayerShip(int xSize, int ySize, String spriteSheetPath, int spriteSize) throws SlickException {
		super(xSize, ySize, spriteSheetPath, spriteSize);
	}
	@Override
	public void update(int delta, Input input){

		Vector2f temp = new Vector2f(velocity);
		temp.scale(delta);
		position.add(temp);
		
		
		angle += angularVelocity * delta;	// Update rotation
		
		if(angularVelocity > 10){			// Clamp rotation speed
			angularVelocity = 10;
		}else if(angularVelocity < -10){
			angularVelocity = -10;
		}
		
		if(angle > 360){					// Keep rotation between 0 and 360 
			angle -= 360;
		} else if(angle < 0 ){
			angle += 360;
		}
		
		pts.clear();
		for (int i = 0; i < ship.length; i++) { // Thrust 
			for (int j = 0; j < ship[0].length; j++) {
				Vector2f thrustWeight = ship[i][j].getThrust(thrustMultiplier, input);
				if(thrustWeight == null){
					continue;
				}
				
				Vector2f tDirection = thrustWeight;
				tDirection.add(angle);
				
				Vector2f tPosition = Utility.getVectorFromArrayLocation(i, j, shipSize, ship);
				tPosition = getRotatedPoint(tPosition, angle);
				
				if(tDirection.length() == 0){
					continue;
				}
				
				
				pts.add(new Vector2f[]{tPosition, tDirection});
				applyForce(tPosition, tDirection);

			}
		}
		if(input.isKeyDown(Input.KEY_R)){
			angularVelocity = 0;
		}
		
		
		if(changed){		// Update center of mass and ship physics
			updateBlocks();
			hull.update();
			changed = false;
		}
		
	}
	
	@Override
	public void render(GameContainer gc, Graphics g, Rectangle camera) {
		if(positionOnScreen == null){
			positionOnScreen = new Vector2f(gc.getWidth() / 2, gc.getHeight() / 2);
		}
		for (int i = 0; i < ship.length; i++) {			// Draw each ship component
			for (int j = 0; j < ship[0].length; j++) {
				Image curImage = hullList[i][j];
				if(curImage == null){
					continue;
				}
				Vector2f p = Utility.getVectorFromArrayLocation(i, j, shipSize, ship);
				curImage.setCenterOfRotation(center.x - p.x, center.y - p.y);
				curImage.setRotation((float) angle);
				curImage.draw(positionOnScreen.x - center.x + p.x, positionOnScreen.y - center.y + p.y, (float) shipSize, (float) shipSize);
			}
		}
		if(debug){
			g.setColor(Color.blue);
			g.setLineWidth(3);
			g.draw(getHull().transform(Transform.createRotateTransform((float) rAngle())).transform(Transform.createTranslateTransform(positionOnScreen.x, positionOnScreen.y)));
			g.resetLineWidth();
			
			g.setColor(Color.green);
			if(pts != null){
				for (Vector2f[] vs : pts) {
					g.drawOval(positionOnScreen.x + vs[0].x, positionOnScreen.y + vs[0].y, 2, 2);
					vs[1].scale(8000);
					g.drawLine(positionOnScreen.x + vs[0].x, positionOnScreen.y + vs[0].y, positionOnScreen.x + vs[0].x + vs[1].x, positionOnScreen.y + vs[0].y + vs[1].y);
				}
			}
		}
	}
}
