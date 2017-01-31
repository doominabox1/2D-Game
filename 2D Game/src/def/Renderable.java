package def;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public interface Renderable {
	public void render(GameContainer gc, Graphics g, Rectangle camera);
}
