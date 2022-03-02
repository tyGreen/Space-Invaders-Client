//===================================================================================================
// CLIENT-SIDE
//===================================================================================================

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JLabel;

public class ProjectilePlayer extends Sprite {
	
	//Attributes:
	private Thread thread;
	private JLabel lbl_prjct_player, lbl_currentScore;
	private Enemy[][] enemies;
	private Boolean collision, keyPressed, invasionStopped;
	private Player myPlayer;
	private int enemyCount;
	private UFO myUFO;
	
	public int getEnemyCount() {
		return enemyCount;
	}
	public void setEnemyCount(int enemyCount) {
		this.enemyCount = enemyCount;
	}
	public Boolean getCollision() {return collision;}
	public void setCollision(Boolean collision) {this.collision = collision;}
	
	public Boolean getKeyPressed() {return keyPressed;}
	public void setKeyPressed(Boolean keyPressed) {this.keyPressed = keyPressed;}
	
	public JLabel getLbl_currentScore() {return lbl_currentScore;}
	public void setLbl_currentScore(JLabel lbl_currentScore) {this.lbl_currentScore = lbl_currentScore;}
	
	//When passing in arguments via setters, DO NOT alter or create new constructors for the argument - setter only!
	public void setEnemies(Enemy[][] temp) {this.enemies = temp;}
	public void setLbl_prjct_player(JLabel temp) {this.lbl_prjct_player = temp;}
	
	public Boolean getInvasionStopped() {return invasionStopped;}
	public void setInvasionStopped(Boolean boardCleared) {this.invasionStopped = boardCleared;}
	
	//Constructors:
	//Default
	public ProjectilePlayer() {
		super(GameProperties.PRJCT_PLAYER_WIDTH, GameProperties.PRJCT_PLAYER_HEIGHT, "img_prjct_player.png", false, false, false);
		this.collision = false;
		this.inMotion = false;
		this.keyPressed = false;
		this.enemyCount = GameProperties.ENEMY_COUNT;
		this.invasionStopped = false;
	}
	
	//Secondary
	public ProjectilePlayer(JLabel temp1, Player temp2, JLabel temp3, UFO temp4) {
		super(GameProperties.PRJCT_PLAYER_WIDTH, GameProperties.PRJCT_PLAYER_HEIGHT, "img_prjct_player.png", false, false, false);
		this.lbl_prjct_player = temp1;
		this.myPlayer = temp2;
		this.lbl_currentScore = temp3;
		this.myUFO = temp4;
		this.collision = false;
		this.inMotion = false;
		this.keyPressed = false;
		this.enemyCount = GameProperties.ENEMY_COUNT;
		this.invasionStopped = false;
	}
	
	//Other methods:
	@Override
	public void hide() {
		this.lbl_prjct_player.setVisible(false);
	}
	@Override
	public void show() {
		this.lbl_prjct_player.setVisible(true);
	}
	
	private void resetPlayerProjectile() {
		this.lbl_prjct_player.setLocation(this.getX(), this.getY());
		//Hides the label:
		this.hide();
	}
	
	public void updatePlayerScore() {
		this.getLbl_currentScore().setText(String.valueOf(myPlayer.getPlayerScore()));
	}
	
	private void destroyEnemy(Enemy enemy) {
		enemy.hide();
	}
	
	
	public void detectUFOCollision() {
		resetPlayerProjectile();
	}
	
	public void playSoundEffect(int i) {
		int sfx_id = i;
		URL url = this.getClass().getClassLoader().getResource("");
	      try {
	    	  // Play sound effect corresponding to id passed:
	    	  if(sfx_id == 1) {
			     url = this.getClass().getClassLoader().getResource("sfx_projectilePlayer.wav");
	    	  }
	    	  else {
				 url = this.getClass().getClassLoader().getResource("sfx_enemyExplosion.wav");
	    	  }

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
}