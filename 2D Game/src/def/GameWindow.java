package def;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import ships.AIShip;
import ships.PlayerShip;
import ships.ShipPart;
import util.PriorityArrayList;

public class GameWindow extends BasicGame  implements InputListener{
	public GameWindow(String gamename){
		super(gamename);
	}
	Input input;
	PlayerShip playerShip;
	AIShip[] aiShip;
	Rectangle camera;
	PriorityArrayList renderList = new PriorityArrayList();
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		input = new Input(gc.getHeight());
		camera = new Rectangle(0, 0, gc.getWidth(), gc.getHeight());
		
		char[][] s =  {
				{'n', 'n', 'n', 'n', 'n', 'n', 'n'},
				{'8', '7', '7', 'h', '1', '1', '2'},
				{'8', 'u', 'h', 'h', 'h', 'h', '2'},
				{'8', '9', '9', 'h', '3', '3', '2'},
				{'n', 'n', 'n', 'n', 'n', 'n', 'n'},
		};
		
		playerShip = new PlayerShip(s.length, s[0].length, "res/tilesets/mc.png", 32);
		ShipPart sp;
		
//		playerShip = new PlayerShip(3, 3, "res/tilesets/mc.png", 32);
//		ShipPart sp;
//		
//		sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.EAST);
//		sp.addKeyMap(Input.KEY_RIGHT, 1);
//		playerShip.setPart(sp, 1, 1);
//		
//		sp = new ShipPart(ShipPart.TIER2, ShipPart.NOTHING, ShipPart.NOTHING, ShipPart.NOTHING);
//		playerShip.setPart(sp, 2, 1);
		
		
		for (int i = 0; i < s.length; i++) {
			for (int j = 0; j < s[0].length; j++) {
				if(s[i][j] == 'h'){
					sp = new ShipPart(ShipPart.TIER2, ShipPart.NOTHING, ShipPart.NOTHING, ShipPart.NOTHING);
					playerShip.setPart(sp, i, j);
				}else if(s[i][j] == 'u'){
					sp = new ShipPart(ShipPart.TIER3, ShipPart.NOTHING, ShipPart.NOTHING, ShipPart.NOTHING);
					playerShip.setPart(sp, i, j);
				} if(s[i][j] == '8'){
					sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.SOUTH);
					sp.addKeyMap(Input.KEY_DOWN, 1);
					playerShip.setPart(sp, i, j);
				}else if(s[i][j] == '7'){
					sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.EAST);
					sp.addKeyMap(Input.KEY_RIGHT, 1);
					playerShip.setPart(sp, i, j);
				}else if(s[i][j] == '9'){
					sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.WEST);
					sp.addKeyMap(Input.KEY_LEFT, 1);
					playerShip.setPart(sp, i, j);
				}else if(s[i][j] == '1'){
					sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.EAST);
					sp.addKeyMap(Input.KEY_LEFT, 1);
					playerShip.setPart(sp, i, j);
				}else if(s[i][j] == '3'){
					sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.WEST);
					sp.addKeyMap(Input.KEY_RIGHT, 1);
					playerShip.setPart(sp, i, j);
				}else if(s[i][j] == '2'){
					sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.NORTH);
					sp.addKeyMap(Input.KEY_UP, 1);
					playerShip.setPart(sp, i, j);
				}else if(s[i][j] == 'n'){
					sp = new ShipPart();
					playerShip.setPart(sp, i, j);
				}
			}
		}
		
		Background bg = new Background("res/tilesets/stars.png", playerShip.getPositionObject());
		
		aiShip = new AIShip[1];
		for(int i = 0; i < 1; i++){
			aiShip[i] = new AIShip(3, 3, "res/tilesets/mc.png", 32);
			
			sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.EAST);
			aiShip[i].setPart(sp, 1, 0);
			
			sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.EAST);
			aiShip[i].setPart(sp, 1, 2);
			
			sp = new ShipPart(ShipPart.TIER1, ShipPart.NOTHING, ShipPart.TIER1, ShipPart.EAST);
			aiShip[i].setPart(sp, 0, 1);
			
			sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.WEST);
			aiShip[i].setPart(sp, 2, 1);
			
			sp = new ShipPart(ShipPart.TIER1, ShipPart.NOTHING, ShipPart.TIER1, ShipPart.NORTH);
			aiShip[i].setPart(sp, 1, 1);
			
			aiShip[i].initFunctions();
			aiShip[i].setPosition(i * 200, i * 200);
			renderList.addOrdered(aiShip[i]);
		}
		
//		aiShip[0] = AIShip.getRandomAIShip(6, 6, "res/tilesets/mc.png", 32);
//		
//		aiShip[0].initFunctions();
//		aiShip[0].setPosition(200, 200);
		renderList.addOrdered(aiShip[0]);
		
		renderList.addOrdered(bg);
		renderList.addOrdered(playerShip);
	}
	
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		input.poll(gc.getWidth(), gc.getHeight());
		playerShip.update(delta, input);
		for (AIShip ais : aiShip) {
			ais.setTarget(playerShip.getPosition().add(new Vector2f(camera.getWidth() / 2, camera.getHeight() / 2)), playerShip.getVelocity());
			ais.update(delta, input);
		}
		camera.setLocation(playerShip.getPosition());
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException{
		for( Renderable r : renderList){
			r.render(gc, g, camera);
		}
	}
	
	@Override
	public void keyPressed(int key, char c){
		if(key == Input.KEY_ESCAPE){
			System.exit(0);
		} else if(key == Input.KEY_SPACE){
			playerShip.setPosition(500, 500);
			playerShip.setVelocity( new Vector2f(0, 0));
		} else if(key == Input.KEY_N){
			ShipPart sp = new ShipPart(ShipPart.TIER1, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.NORTH);
			sp.addKeyMap(Input.KEY_NUMPAD2, 1);
			playerShip.setPart(sp, 0, 0);
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
