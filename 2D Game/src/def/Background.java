package def;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class Background implements Renderable{
	Vector2f position;
	Image image;
	public Background(String imagePath, Vector2f position) throws SlickException{
		this.position = position;
		image = new Image(imagePath);
	}
	@Override
	public void render(GameContainer gc, Graphics g) { // Background image must be in powers of two to wrap properly (2048 seems to work best)
		g.drawImage(image, 0, 0, gc.getWidth(), gc.getHeight(), position.x, position.y, position.x + gc.getWidth(), position.y + gc.getHeight());
	}
}
