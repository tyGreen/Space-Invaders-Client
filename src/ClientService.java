import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

//===================================================================================================
// CLIENT-SIDE
//===================================================================================================

// Command-processing Service on the Client-side
	// (Processes commands from Server?)

public class ClientService implements Runnable{
	
	// Server
//	final int SERVER_PORT = 5557;
	private Socket s;
	private Scanner in;
		
	private GameHandler gameHandler;
	
	// Sprites
	private Player player;
	private ProjectilePlayer playerProjectile;
	private Enemy[][] enemies;
	private UFO ufo;
	
	// Graphics
	private JLabel lbl_player, lbl_playerProjectile, lbl_score, lbl_currentScore, lbl_ufo;
	private ImageIcon img_player, img_enemy, img_playerProjectile, img_enemyProjectile, img_ufo;
	private JLabel[] lbl_playerLives;

	public ClientService (Socket s, Player player, ProjectilePlayer playerProjectile, 
							Enemy[][] enemies, UFO ufo,
							JLabel lbl_player, JLabel lbl_playerProjectile, JLabel lbl_score,
							JLabel lbl_currentScore, JLabel lbl_ufo, ImageIcon img_player,
							ImageIcon img_enemy, ImageIcon img_playerProjectile, ImageIcon img_enemyProjectile, ImageIcon img_ufo, 
							JLabel[] lbl_playerLives, GameHandler gameHandler) {
		this.s = s;
		this.player = player;
		this.playerProjectile = playerProjectile;
		this.enemies = enemies;
		this.ufo = ufo;
		this.lbl_player = lbl_player;
		this.lbl_playerProjectile = lbl_playerProjectile;
		this.lbl_currentScore = lbl_currentScore;
		this.lbl_ufo = lbl_ufo;
		this.img_player = img_player;
		this.img_enemy = img_enemy;
		this.img_playerProjectile = img_playerProjectile;
		this.img_enemyProjectile = img_enemyProjectile;
		this.lbl_playerLives = lbl_playerLives;
		this.gameHandler = gameHandler;
	}
	
	public void run() {
		
		try {
			in = new Scanner(s.getInputStream());
			processRequest( );
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			try {
				if(s != null) {
					s.close();

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	// Processing the requests
	public void processRequest () throws IOException {
		// If next request is empty then return
		while(true) {
			if(!in.hasNext( )){
				if(s != null) {
					try{
						s.close();
					}
					catch(Exception e) {
						e.printStackTrace();
					}

				}
				return;
			}
			String command = in.next();
			if (command.equals("QUIT")) {
				if(s != null) {
					try{
						s.close();
					}
					catch(Exception e) {
						e.printStackTrace();
					}

				}
				return;
			} else {
				executeCommand(command);
			}
		}
	}
	
	public void executeCommand(String command) throws IOException{
	
		if(command.equals("UPDATE_PLAYER_LBL")) {
			int updatedX = in.nextInt();
			int updatedY = in.nextInt();
			Boolean updatedCanMove = in.nextBoolean();
			player.setX(updatedX);
			player.setY(updatedY);
			player.hitbox.setSize(0, 0);
			player.setCanMove(updatedCanMove);
			lbl_player.setLocation(player.getX(), player.getY());
		}
		else if(command.equals("UPDATE_PLAYER_PROJECTILE_LBL")) {
			int updatedX = in.nextInt();
			int updatedY = in.nextInt();
//			Boolean currentMotion = in.hasNextBoolean();
			Boolean currentMotion = in.nextBoolean();
			playerProjectile.setX(updatedX);
			playerProjectile.setY(updatedY);
			lbl_playerProjectile.setLocation(playerProjectile.getX(), playerProjectile.getY());
			if(currentMotion) {
				lbl_playerProjectile.setVisible(true);
			}
			else {
				lbl_playerProjectile.setVisible(false);
			}
		}
		else if(command.equals("UPDATE_ENEMY_LBLS")) {
			for(int i = 0; i < GameProperties.ENEMY_ROWS; i++) {
				for(int j = 0; j < GameProperties.ENEMY_COLS; j++) {
					int updatedX = in.nextInt();
					int updatedY = in.nextInt();
					Boolean updatedMotion = in.nextBoolean();
					enemies[i][j].setX(updatedX);
					enemies[i][j].setY(updatedY);
					enemies[i][j].getLbl_enemy().setLocation(enemies[i][j].getX(), enemies[i][j].getY());
					// Determine visibility
					if(!updatedMotion && !gameHandler.getGameOver()) {
						enemies[i][j].getLbl_enemy().setVisible(false);
					}
				}
			}			
		}
		else if(command.equals("UPDATE_ENEMY_PROJECTILE_LBLS")) {
			for(int i = 0; i < GameProperties.ENEMY_ROWS; i++) {
				for(int j = 0; j < GameProperties.ENEMY_COLS; j++) {
					int updatedX = in.nextInt();
					int updatedY = in.nextInt();
					Boolean currentMotion = in.nextBoolean();
					enemies[i][j].getEnemyProjectile().setX(updatedX);
					enemies[i][j].getEnemyProjectile().setY(updatedY);
					enemies[i][j].getEnemyProjectile().getLbl_prjct_enemy().setLocation(enemies[i][j].getEnemyProjectile().getX(), enemies[i][j].getEnemyProjectile().getY());
					// Determine visibility
					if(currentMotion) {
						enemies[i][j].getEnemyProjectile().getLbl_prjct_enemy().setVisible(true);

					}
					else {
						enemies[i][j].getEnemyProjectile().getLbl_prjct_enemy().setVisible(false);
					}
				}
			}	
		}
		else if(command.equals("UPDATE_UFO_LBL")) {
			int updatedX = in.nextInt();
			int updatedY = in.nextInt();
			Boolean updatedInMotion = in.nextBoolean();
			ufo.setX(updatedX);
			ufo.setY(updatedY);
			lbl_ufo.setLocation(ufo.getX(), ufo.getY());
			ufo.setInMotion(updatedInMotion);
			// Determine if sfx should play
			if(ufo.getX() > 0) {
				ufo.playSoundEffect();
			}
		}
		else if(command.equals("UPDATE_PLAYER_LIVES_LBL")) {
			int updatedPlayerLives = in.nextInt();
			player.setPlayerLives(updatedPlayerLives);
			if(updatedPlayerLives < 3) {
				enemies[0][0].getEnemyProjectile().subtractPlayerLife();
			}
		}
		else if(command.equals("SEND_GAMEOVER")) {
				gameHandler.gameOverRoutine();
		}
		else if(command.equals("SEND_INVASION_STOPPED")) {
				gameHandler.gameWonRoutine();
		}
		else if(command.equals("UPDATE_SCORE_LBL")) {
			int updatedPlayerScore = in.nextInt();
			player.setPlayerScore(updatedPlayerScore);
			playerProjectile.updatePlayerScore();		
			}
		else if(command.equals("SEND_PLAYER_EXPLOSION")) {
			enemies[0][0].getEnemyProjectile().resetPlayer();
			enemies[0][0].getEnemyProjectile().playSoundEffect();
		}
		else if(command.equals("PLAY_ENEMY_EXPLOSION")) {
			playerProjectile.playSoundEffect(2);
		}
		else {
			System.exit(0);
		}
	}
}
