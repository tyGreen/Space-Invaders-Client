import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class GameHandler {
	
	private Boolean gameOver;
	
	Player player;
	ProjectilePlayer playerProjectile;
	Enemy[][] enemies;
	UFO ufo;
	JLabel lbl_player, lbl_playerProjectile, lbl_score, lbl_currentScore, lbl_ufo;
	ImageIcon img_player, img_enemy, img_playerProjectile, img_enemyProjectile, img_ufo;
	JLabel[] lbl_playerLives;

	public Boolean getGameOver() {return gameOver;}
	public void setGameOver(Boolean gameOver) {this.gameOver = gameOver;}
	
	GameHandler() {
		this.gameOver = false;
	}
	
	GameHandler(Player player, ProjectilePlayer playerProjectile, 
				Enemy[][] enemies, UFO ufo,
				JLabel lbl_player, JLabel lbl_playerProjectile, JLabel lbl_score,
				JLabel lbl_currentScore, JLabel lbl_ufo, ImageIcon img_player,
				ImageIcon img_enemy, ImageIcon img_playerProjectile, ImageIcon img_enemyProjectile, ImageIcon img_ufo, 
				JLabel[] lbl_playerLives) {
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
		this.gameOver = false;
	}
	
	public void stopGame() {
		// Stop the player & its projectile:
		player.stop();
		playerProjectile.stop();
		
		// Hide player projectile:
//		lbl_prjct_player.setVisible(false);
		
		// Stop UFO spawning/movement:
		ufo.setStopThread(true);
		
		// Stop enemies & their projectiles, & hide enemy projectiles:
		for(int i = 0; i < GameProperties.ENEMY_ROWS; i++) {
			for(int j = 0; j < GameProperties.ENEMY_COLS; j++) {
				enemies[i][j].stop();
				enemies[i][j].setCanShoot(false);
				enemies[i][j].getEnemyProjectile().setStopProjectile(true);
//				enemies[i][j].getEnemyProjectile().getLbl_prjct_enemy().setVisible(false);	
			}
		}
	}
	
	public static void displayScoreboard(ResultSet rs) throws SQLException {
		String[] names = new String[10];
		int[] scores = new int[10];
		int i = 0;
		// While still more records:
		while(rs.next()) {
			// Store name at next index of name array, and score at next index of score array:
			names[i] = rs.getString("name");
			scores[i] = rs.getInt("score");	
			i++;
		}
		JOptionPane.showMessageDialog(null, "Leaderboard: \n 1. " 
				+ names[0] + "  " + scores[0] + "\n2. "
				+ names[1] + "  " + scores[1] + "\n3. "
				+ names[2] + "  " + scores[2] + "\n4. "
				+ names[3] + "  " + scores[3] + "\n5. "
				+ names[4] + "  " + scores[4] + "\n6. "
				+ names[5] + "  " + scores[5] + "\n7. "
				+ names[6] + "  " + scores[6] + "\n8. "
				+ names[7] + "  " + scores[7] + "\n9. "
				+ names[8] + "  " + scores[8] + "\n10. "
				+ names[9] + "  " + scores[9]	
		);
	}
	
	public static void submitScore(String name, int score) {
		//Declare connection & SQL statement
		String playerName = name;
		int playerScore = score;
		Connection conn = null;
		Statement stmt = null;
		
		try {
			Class.forName("org.sqlite.JDBC"); //JDBC -> Java DataBase Connectivity
			System.out.println("Database Driver Loaded");
			
			String dbURL = "jdbc:sqlite:space-invaders.db"; // db file created in bin folder
			conn = DriverManager.getConnection(dbURL);
			
			if(conn != null) {
				//If db connection was successful:
				System.out.println("Connected to database");
				conn.setAutoCommit(false);
				
				DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
				System.out.println("Driver name: " + dm.getDriverName());
				System.out.println("Driver version: " + dm.getDriverVersion());
				System.out.println("Product name: " + dm.getDatabaseProductName());
				System.out.println("Product version: " + dm.getDatabaseProductVersion());
				
				// Create "SCOREBOARD" table:
				stmt = conn.createStatement();
				
				String sql = "CREATE TABLE IF NOT EXISTS SCOREBOARD" +
							 "(ID INTEGER PRIMARY KEY AUTOINCREMENT," + //AUTOINCREMENT requires INTEGER (not INT)
							 "NAME TEXT NOT NULL," +
							 "SCORE INT NOT NULL)";
				stmt.executeUpdate(sql);
				conn.commit();
				System.out.println("Table Created Successfully");
				
				sql = "INSERT INTO SCOREBOARD (NAME, SCORE) VALUES (?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, playerName);
				pstmt.setInt(2, playerScore);
				pstmt.executeUpdate();
				conn.commit();
				
				// Select 10 highest scores (& corresponding player name) from db:
				ResultSet rs = stmt.executeQuery("SELECT * FROM SCOREBOARD ORDER BY SCORE DESC LIMIT 10");
				displayScoreboard(rs); // Displays top scores
				rs.close(); //close results-set to free resources
			
				conn.close(); //closes connection to db file	
			}
			}
			catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
	}
	
	public void gameOverRoutine() {
//		 Display "GAME OVER" msg:
		JOptionPane.showMessageDialog(null, "GAME OVER");
		String playerName = JOptionPane.showInputDialog("Please enter your name: ");
		while(playerName.trim().isEmpty()) {
			playerName = JOptionPane.showInputDialog("Please enter your name: ");
		}
		submitScore(playerName, player.getPlayerScore());
		System.exit(0);
	}
	
	public int awardLifeBonus(int score, int lives) {
		int preBonusScore = score;
		int numLivesRemaining = lives;
		int bonusAwarded = 0;

		// Adjust bonus pts awarded based on num lives remaining:
		if(numLivesRemaining == 3) {
			bonusAwarded = GameProperties.NO_HIT_BONUS;
		}
		else {
			bonusAwarded = numLivesRemaining * GameProperties.PTS_PER_BONUS_LIFE;
		}
		
		return (preBonusScore + bonusAwarded);
	}
	
	public void gameWonRoutine() {
		// Display congratulatory msg:
		JOptionPane.showMessageDialog(null, "YOU STOPPED THE IVASION!");
		String playerName = JOptionPane.showInputDialog("Please enter your name: ");
		
		while(playerName.trim().isEmpty()) {
			playerName = JOptionPane.showInputDialog("Please enter your name: ");
		}
		
		submitScore(playerName, player.getPlayerScore());
		System.exit(0);
	}
}
