package def;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import ships.AIShip;
import ships.PlayerShip;

public class GameWindow extends BasicGame  implements InputListener{
	public GameWindow(String gamename){
		super(gamename);
	}
	Input input;
	PlayerShip ship;
	AIShip aiShip;
	ArrayList<Renderable> renderList = new ArrayList<Renderable>();
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		
		char[][] s =  {
				{'7', '7', '8', 'h', '2', '1', '1'},
				{'n', 'h', '8', 'h', '2', 'h', 'n'},
				{'9', '9', '8', 'h', '2', '3', '3'},
		};
		
		input = new Input(gc.getHeight());
		
		ship = new PlayerShip(s.length, s[0].length, "res/tilesets/mc.png", 32);
		ShipPart sp;
		
		for (int i = 0; i < s.length; i++) {
			for (int j = 0; j < s[0].length; j++) {
				if(s[i][j] == 'h'){
					sp = new ShipPart(ShipPart.TIER2, ShipPart.NOTHING, ShipPart.NOTHING, ShipPart.NOTHING);
					ship.setPart(sp, i, j);
				}else if(s[i][j] == '8'){
					sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.SOUTH);
					sp.addKeyMap(Input.KEY_UP, 1);
					ship.setPart(sp, i, j);
				}else if(s[i][j] == '7'){
					sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.EAST);
					sp.addKeyMap(Input.KEY_RIGHT, 1);
					ship.setPart(sp, i, j);
				}else if(s[i][j] == '9'){
					sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.WEST);
					sp.addKeyMap(Input.KEY_LEFT, 1);
					ship.setPart(sp, i, j);
				}else if(s[i][j] == '1'){
					sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.EAST);
					sp.addKeyMap(Input.KEY_LEFT, 1);
					ship.setPart(sp, i, j);
				}else if(s[i][j] == '3'){
					sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.WEST);
					sp.addKeyMap(Input.KEY_RIGHT, 1);
					ship.setPart(sp, i, j);
				}else if(s[i][j] == '2'){
					sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.NORTH);
					sp.addKeyMap(Input.KEY_DOWN, 1);
					ship.setPart(sp, i, j);
				}else if(s[i][j] == 'n'){
					sp = new ShipPart();
					ship.setPart(sp, i, j);
				}
			}
		}
		
		Background bg = new Background("res/tilesets/stars.png", ship.getPositionObject());
		
		aiShip = new AIShip(3, 3, "res/tilesets/mc.png", 32);
		
		sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.EAST);
		aiShip.setPart(sp, 1, 0);
		
		sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.EAST);
		aiShip.setPart(sp, 1, 2);
		
		sp = new ShipPart(ShipPart.TIER1, ShipPart.NOTHING, ShipPart.TIER1, ShipPart.EAST);
		aiShip.setPart(sp, 2, 1);
		
		sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.WEST);
		aiShip.setPart(sp, 0, 1);
		
		sp = new ShipPart(ShipPart.TIER1, ShipPart.NOTHING, ShipPart.TIER1, ShipPart.NORTH);
		aiShip.setPart(sp, 1, 1);
		
		aiShip.initFunctions();
		aiShip.setPosition(500, 500);
		
		renderList.add(bg);
		//renderList.add(ship);
		renderList.add(aiShip);
	}
	
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		input.poll(gc.getWidth(), gc.getHeight());
		//ship.setPosition(input.getMouseX(), input.getMouseY());
		//ship.update(delta, input);
		aiShip.update(delta, input);
		
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException{
		g.scale(0.5f, 0.5f);
		g.translate(gc.getWidth() / 2, gc.getHeight() / 2);
		for( Renderable r : renderList){
			r.render(gc, g);
		}
		
	}
	
	@Override
	public void keyPressed(int key, char c){
		if(key == Input.KEY_ESCAPE){
			System.exit(0);
		} else if(key == Input.KEY_SPACE){
			ship.setPosition(500, 500);
			ship.setVelocity( new Vector2f(0, 0));
		} else if(key == Input.KEY_N){
			ShipPart sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.NORTH);
			sp.addKeyMap(Input.KEY_NUMPAD2, 1);
			ship.setPart(sp, 0, 0);
		}
	}

	public static void main(String[] args){
		try{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new GameWindow("Space"));
			appgc.setTargetFrameRate(60);
			appgc.setDisplayMode(1280, 720, false);
			appgc.start();
		} catch (SlickException ex){
			Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
