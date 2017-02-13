package def;

import interfaces.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Background implements Renderable{
	Vector2f position;
	Image image;
	int priority;
	public Background(String imagePath, Vector2f position) throws SlickException{
		this.position = position;
		image = new Image(imagePath);
		priority = -1000000;
	}
	@Override
	public void render(GameContainer gc, Graphics g, Rectangle camera) { // Background image must be in powers of two to wrap properly (2048 seems to work best)
		g.drawImage(image, 0, 0, gc.getWidth(), gc.getHeight(), position.x, position.y, position.x + gc.getWidth(), position.y + gc.getHeight());
	}
	@Override
	public int getRenderPriority() {
		return priority;
	}
	@Override
	public void setRenderPriority(int newPriority) {
		priority = newPriority;
	}
}
