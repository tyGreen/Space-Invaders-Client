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

public class UFO extends Sprite {
	private JLabel lbl_UFO;
	private Boolean stopThread, isAlive;
	
	// Getters & Setters:
	
	public Boolean getIsAlive() {return isAlive;}
	public void setIsAlive(Boolean isAlive) {this.isAlive = isAlive;}
	
	public Boolean getStopThread() {return stopThread;}
	public void setStopThread(Boolean stopThread) {this.stopThread = stopThread;}

	//Constructors:
	public UFO() {
		super(GameProperties.UFO_WIDTH, GameProperties.UFO_HEIGHT, "img_UFO.png", true, true, false);
		this.stopThread = false;
		this.isAlive = true;
	}
	
	public UFO(JLabel temp1) {
		super(GameProperties.UFO_WIDTH, GameProperties.UFO_HEIGHT, "img_UFO.png", true, true, false);
		this.lbl_UFO = temp1;
		this.stopThread = false;
		this.isAlive = true;
	}
	
	// UFO Methods:
	
	public void resetUFO() {
		this.lbl_UFO.setLocation(this.getX(), this.getY());
	}
	
	public void playSoundEffect() {
		try {
			// Retrieve sound file & store in var:
			URL url = this.getClass().getClassLoader().getResource("sfx_ufo2.wav");

			// Open audio input stream:
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
 
			// Retrieve sound clip resource:
			Clip clip = AudioSystem.getClip();
 
			// Open clip and load sample from input stream:
			clip.open(audioIn);
			clip.loop(1);
			
	      } catch (UnsupportedAudioFileException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (LineUnavailableException e) {
	         e.printStackTrace();
	      }
	  }	
	
} // End Class
