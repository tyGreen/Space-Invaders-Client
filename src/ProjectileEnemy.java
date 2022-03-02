//===================================================================================================
// CLIENT-SIDE
//===================================================================================================

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

public class ProjectileEnemy extends Sprite {
	
	//Attributes:
	private JLabel lbl_prjct_enemy, lbl_player;
	private Player myPlayer;
	private Timer tmr_regeneratePlayer;
	private Enemy myEnemy;
	private Boolean stopProjectile, gameOver;
	private JLabel[] lbl_playerLives;
	private Enemy[][] enemies;
	
	public Boolean getStopProjectile() {return stopProjectile;}
	public void setStopProjectile(Boolean temp) {this.stopProjectile = temp;}
	
	public Boolean getGameOver() {return gameOver;}
	public void setGameOver(Boolean gameOver) {this.gameOver = gameOver;}
	
	public JLabel getLbl_prjct_enemy() {return lbl_prjct_enemy;}
	public void setLbl_prjct_enemy(JLabel lbl_prjct_enemy) {this.lbl_prjct_enemy = lbl_prjct_enemy;}
	
	//When passing in arguments via setters, DO NOT alter or create new constructors for the argument - setter only!
	public void setPlayer(Player temp) {this.myPlayer = temp;}
	public void setLbl_player(JLabel temp) {this.lbl_player = temp;}
	public void setEnemy(Enemy temp) {this.myEnemy = temp;}

	//Constructors:
	//Default
	public ProjectileEnemy() {
		super(GameProperties.PRJCT_ENEMY_WIDTH, GameProperties.PRJCT_ENEMY_HEIGHT, "img_prjct_enemy.png", false, false, false);
		this.stopProjectile = false;
		this.gameOver = false;
		this.lbl_prjct_enemy = new JLabel();
	}
	
	public ProjectileEnemy(Enemy temp1, JLabel[] temp2, Player temp3, Enemy[][] temp4) {
		super(GameProperties.PRJCT_ENEMY_WIDTH, GameProperties.PRJCT_ENEMY_HEIGHT, "img_prjct_enemy.png", false, false, false);
		this.myEnemy = temp1;
		this.lbl_playerLives = temp2;
		this.myPlayer = temp3;
		this.enemies = temp4;
		this.stopProjectile = false;
		this.gameOver  = false;
		this.lbl_prjct_enemy = new JLabel();
	}
	
	//Other methods:
	@Override
	public void hide() {
		lbl_prjct_enemy.setVisible(false);
	}
	
	@Override
	public void show() {
		lbl_prjct_enemy.setVisible(true);
	}
	
	public void launchProjectile() {
		// Make projectile (label) visisble:
		this.lbl_prjct_enemy.setVisible(true);
		// Update label to match new coordinate(s):
		this.lbl_prjct_enemy.setLocation(this.x, this.y);
	}
	
	public void resetProjectile() {
		// Hide the projectile (label):
		this.lbl_prjct_enemy.setVisible(false);
		// Set "in motion" flag to false (important so coordinates can sync w/ corresponding enemy):
		this.lbl_prjct_enemy.setLocation(this.x, this.y);
	}
	
	public void subtractPlayerLife() {		
		// Update player lives label to reflect current # player lives remaining:
		this.lbl_playerLives[this.myPlayer.getPlayerLives()].setVisible(false);
	}
	
	public void resetPlayer() {
		// Set player icon to explosion:
		myPlayer.getLbl_player().setIcon(new ImageIcon(getClass().getResource("img_explosion_player.gif")));
		// After delay of 1.5 seconds:
		tmr_regeneratePlayer = new Timer(1500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Restore the hitbox, key controls, & icon:
				myPlayer.getLbl_player().setIcon(new ImageIcon(getClass().getResource("img_player.png")));	
			}
		});
		tmr_regeneratePlayer.start();
	}
	
	public void playSoundEffect() {
		try {
			// Retrieve sound file & store in var:
			URL url = this.getClass().getClassLoader().getResource("sfx_playerExplosion.wav");

			// Open audio input stream:
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
 
			// Retrieve sound clip resource:
			Clip clip = AudioSystem.getClip();
 
			// Open clip and load sample from input stream:
			clip.open(audioIn);
			clip.start();
	         
	      } catch (UnsupportedAudioFileException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (LineUnavailableException e) {
	         e.printStackTrace();
	      }
	  }
	
} // End ProjectileEnemy.java