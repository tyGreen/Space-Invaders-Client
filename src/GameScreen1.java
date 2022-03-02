//===================================================================================================
// CLIENT-SIDE
//===================================================================================================

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class GameScreen1 extends JFrame implements KeyListener{

	private static final long serialVersionUID = -8824959153681940829L;
		
	private GameHandler gameHandler;
	
	// Sockets & Server Vars
	final static int CLIENT_PORT = 5555; 
	final static int SERVER_PORT = 5557;
	static Socket s;
	static PrintWriter out;
	String command = "";
	
	//Storage classes for game sprites:
	private Player player;
	private ProjectilePlayer playerProjectile;
	protected Enemy[][] enemies;
	protected UFO ufo;
	
	//JLabels to display sprites:
	protected JLabel lbl_player, lbl_playerProjectile, lbl_score, lbl_currentScore, lbl_ufo;
	protected ImageIcon img_player, img_enemy, img_playerProjectile, img_enemyProjectile, img_ufo;
	protected JLabel[] lbl_playerLives;
		
	//Graphics container:
	protected Container container1;
	
// ==================================================================================================
// GUI CONSTRUCTOR (BUILDS GAME-BOARD)
// ==================================================================================================
	//GUI set-up (constructor):
	public GameScreen1() {
		//Window title:
		super("Space Invaders");
		//Screen size:
		setSize(GameProperties.SCREEN_WIDTH, GameProperties.SCREEN_HEIGHT);
		//Centers game window on screen upon launching:
		setLocationRelativeTo(null);
		
		lbl_player = new JLabel();
		player = new Player(lbl_player);
		System.out.println("Player FileName: " + player.getFileName());
		img_player = new ImageIcon(getClass().getResource(player.getFileName()));
		lbl_player.setIcon(img_player);
		lbl_player.setSize(player.getWidth(), player.getHeight());
		
		//SCOREBOARD:
		//"SCORE" label:
		lbl_score = new JLabel("SCORE");
		lbl_score.setFont(new Font("Serif", Font.BOLD, GameProperties.SCORE_TXT_SIZE));
		lbl_score.setForeground(Color.white);
		lbl_score.setSize(70, GameProperties.SCORE_TXT_SIZE);
		//Displays current score:
		lbl_currentScore = new JLabel(String.valueOf(player.getPlayerScore()));
		lbl_currentScore.setFont(new Font("Serif", Font.ITALIC, GameProperties.SCORE_TXT_SIZE));
		lbl_currentScore.setForeground(Color.white);
		lbl_currentScore.setSize(70, GameProperties.SCORE_TXT_SIZE);
		
		lbl_playerLives = new JLabel[3];
		for(int i = 0; i < 3; i++) {
			lbl_playerLives[i] = new JLabel();
			lbl_playerLives[i].setIcon(new ImageIcon(getClass().getResource("img_playerLifeIcon.png")));
			lbl_playerLives[i].setSize(GameProperties.PLAYER_LIFE_ICON_WIDTH, GameProperties.PLAYER_LIFE_ICON_HEIGHT);
		}
		
		lbl_player = new JLabel();
		player = new Player(lbl_player);
		img_player = new ImageIcon(getClass().getResource(player.getFileName()));
		lbl_player.setIcon(img_player);
		lbl_player.setSize(player.getWidth(), player.getHeight());
		
		lbl_ufo = new JLabel();
		ufo = new UFO(lbl_ufo);
		img_ufo = new ImageIcon(getClass().getResource(ufo.getFileName()));
		lbl_ufo.setIcon(img_ufo);
		lbl_ufo.setSize(ufo.getWidth(), ufo.getHeight());
		
		enemies = new Enemy[GameProperties.ENEMY_ROWS][GameProperties.ENEMY_COLS];
		int enemyOffsetX = 0;
		int enemyOffsetY = 0;
		for(int i = 0; i < GameProperties.ENEMY_ROWS; i++) {
			enemyOffsetX = 0;
			for(int j = 0; j < GameProperties.ENEMY_COLS; j++) {
				enemies[i][j] = new Enemy((0 + enemyOffsetX), (GameProperties.ENEMY_HEIGHT + enemyOffsetY), new JLabel(), player, enemies, lbl_playerLives);
				enemies[i][j].getLbl_enemy().setSize(enemies[i][j].getWidth(), enemies[i][j].getHeight());			
				enemyOffsetX += (enemies[i][j].getWidth() + GameProperties.ENEMY_SPACING);
				
				// Set icon according to row:
				if(i == 0) {
					img_enemy = new ImageIcon(getClass().getResource("img_enemy1.gif"));
					enemies[i][j].setEnemyID(1);
				}
				else if (i == 1 || i == 2) {
					img_enemy = new ImageIcon(getClass().getResource("img_enemy2.gif"));
					enemies[i][j].setEnemyID(2);
				}
				else {
					img_enemy = new ImageIcon(getClass().getResource("img_enemy3.gif"));
					enemies[i][j].setEnemyID(3);
				}
				
				enemies[i][j].getLbl_enemy().setIcon(img_enemy);
				
				// Set the "bumpers" (enemies to reach walls first):
				// If this is the first enemy in the first row (top left corner):
				if((i == 0) && (j == 0)) {
					// It is the left bumper:
					enemies[i][j].setIsLeftBumper(true);
				}
				// Else, if this is the last enemy in the first row (top right corner):
				else if((i == 0) && (j == GameProperties.ENEMY_COLS - 1)) {
					// It is the right bumper:
					enemies[i][j].setIsRightBumper(true);
					// Get focus first because enemies move to right first:
					enemies[i][j].setHasFocus(true);
					
				}
				// Else, if this enemy is the first in the last row (bottom left corner):
				else if((i == (GameProperties.ENEMY_ROWS - 1)) && (j == 0)) {
					// Set "bumper" flag to true:
					enemies[i][j].setIsBottomBumper(true);
				}
				
				// Set "can shoot" flag (allows enemies to launch projectiles):
				// If enemy is in bottom row:
				if(i == GameProperties.ENEMY_ROWS - 1) {
					// Set "can shoot" flag to true:
					enemies[i][j].setCanShoot(true);
				}
				
				// If this enemy is the last in its row:
				if(j == GameProperties.ENEMY_COLS - 1) {
					// Start positioning enemies on next row:
					enemyOffsetY += enemies[i][j].getHeight();
				}
				
				// ENEMY'S PROJECTILE:
				enemies[i][j].getEnemyProjectile().setX(enemies[i][j].getX() + 12); // Enemy projectile sprite half width of enemy sprite, so to be centered behind enemy, sprite needs to be offset by half width of enemy
				enemies[i][j].getEnemyProjectile().setY(enemies[i][j].getY());
				// Set size of enemy projectile label to match size of enemy projectile sprite:
				enemies[i][j].getLbl_enemy().setSize(enemies[i][j].getWidth(), enemies[i][j].getHeight());			

				enemies[i][j].getEnemyProjectile().getLbl_prjct_enemy().setSize(GameProperties.PRJCT_ENEMY_WIDTH, GameProperties.PRJCT_PLAYER_HEIGHT);
				// Set image icon of enemy projectile:
				img_enemyProjectile = new ImageIcon(getClass().getResource(enemies[i][j].getEnemyProjectile().getFileName()));
				enemies[i][j].getEnemyProjectile().getLbl_prjct_enemy().setIcon(img_enemyProjectile);
			}	
		}
		
		lbl_playerProjectile = new JLabel();
		playerProjectile = new ProjectilePlayer(lbl_playerProjectile, player, lbl_currentScore, ufo);
		img_playerProjectile = new ImageIcon(getClass().getResource(playerProjectile.getFileName()));
		lbl_playerProjectile.setIcon(img_playerProjectile);
		lbl_playerProjectile.setSize(playerProjectile.getWidth(), playerProjectile.getHeight());
		playerProjectile.setLbl_prjct_player(lbl_playerProjectile);
		playerProjectile.setEnemies(enemies);

		gameHandler = new GameHandler(player, playerProjectile, enemies, ufo, 
										lbl_player, lbl_playerProjectile, lbl_score,
										 lbl_currentScore, lbl_ufo, img_player,img_enemy, 
										 img_playerProjectile, img_enemyProjectile, img_ufo, 
										 lbl_playerLives);
		
		container1 = getContentPane();
		container1.setBackground(Color.black);
		setLayout(null);
		
		//Set object coordinates:
		player.setX((GameProperties.SCREEN_WIDTH/2) - player.getWidth());
		player.setY(GameProperties.SCREEN_HEIGHT - (player.getHeight() * 2));
		
		ufo.setX(0 - ufo.getWidth());
		ufo.setY(5);
		
		playerProjectile.setX(player.getX() + (playerProjectile.getWidth()/2));
		playerProjectile.setY(player.getY());
		
		//Update lbl positions to match stored values:
		lbl_score.setLocation(15, 15);
		lbl_currentScore.setLocation(15, (15 + GameProperties.SCORE_TXT_SIZE));
		lbl_player.setLocation(player.getX(), player.getY());
		lbl_ufo.setLocation(ufo.getX(), ufo.getY());
		
		int offset_playerLives = 0;
		for(int i = 0; i < lbl_playerLives.length; i++) {
			lbl_playerLives[i].setLocation(offset_playerLives, (GameProperties.SCREEN_HEIGHT - 70));
			offset_playerLives += 30;
		}
		
		lbl_playerProjectile.setLocation(playerProjectile.getX(), playerProjectile.getY());
		
		int offset_enemyX = 0;
		int offset_enemyY = 0;
		for(int i = 0; i < GameProperties.ENEMY_ROWS; i++) {
			offset_enemyX = 0;
			for(int j = 0; j < GameProperties.ENEMY_COLS; j++) {
				enemies[i][j].getLbl_enemy().setLocation(enemies[i][j].getX() + offset_enemyX, enemies[i][j].getY() + offset_enemyY);
				enemies[i][j].getEnemyProjectile().getLbl_prjct_enemy().setLocation(enemies[i][j].getEnemyProjectile().getX(), enemies[i][j].getEnemyProjectile().getY());
				offset_enemyX += (enemies[i][j].getWidth() + GameProperties.ENEMY_SPACING);
				if(j == (GameProperties.ENEMY_COLS)) {
					offset_enemyY += enemies[i][j].getHeight();
				}
			}
		}
				
		//Add objects to screen:
		add(lbl_score);
		add(lbl_currentScore);
		add(lbl_player);
		add(lbl_ufo);
		
		for(int i = 0; i < lbl_playerLives.length; i++) {
			this.add(lbl_playerLives[i]);
		}
		
		add(lbl_playerProjectile);
		
		for(int i = 0; i < GameProperties.ENEMY_ROWS; i++) {
			for(int j = 0; j < GameProperties.ENEMY_COLS; j++) {
				this.add(enemies[i][j].getLbl_enemy()).setVisible(enemies[i][j].getVisible());
				this.add(enemies[i][j].getEnemyProjectile().getLbl_prjct_enemy()).setVisible(false);
			}
		}
		
		lbl_playerProjectile.setVisible(false);
		
		container1.addKeyListener(this);
		container1.setFocusable(true);
		
		try {
			// Socket to reach server
			final ServerSocket client = new ServerSocket(CLIENT_PORT);
					
			// Thread #1 - Server to Listen for Commands from Server
			Thread t1 = new Thread ( new Runnable () {
				public void run ( ) {
					synchronized(this) {
						
						System.out.println("Waiting for server responses...");
						
						while(true) {
							Socket s;
							try {
								s = client.accept();
								System.out.println("server connected");			

								ClientService cService = new ClientService (s, player, playerProjectile, enemies, ufo, 
																			lbl_player, lbl_playerProjectile, lbl_score,
																			 lbl_currentScore, lbl_ufo, img_player,img_enemy, 
																			 img_playerProjectile, img_enemyProjectile, img_ufo, 
																			 lbl_playerLives, gameHandler);
								Thread t = new Thread(cService);
								t.start();
							} 
							catch (IOException e) {
								e.printStackTrace();
							} 		
						}	
					}
				}
			});
			t1.start( );
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		
		// Threads 2 Runs Every .5 Seconds to Request Updates to Sprite Objects Coordinates (x,y)
		// on Server Side to Update Their Corresponding Labels on the Client Side
		try {
			Thread t2 = new Thread ( new Runnable () {
				public void run ( ) {
					synchronized(this) {						
						while(true) {
							try {
								// Set up a communication socket
								Socket s2 = new Socket("localhost", SERVER_PORT);
								
								// Initialize data stream to send data out
								OutputStream outstream = s2.getOutputStream();
								out = new PrintWriter(outstream);
								
								// Command server to send updated player coordinates
								command = "UPDATE_PLAYER\n";
								System.out.println("Sending: " + command);
								out.println(command);
								out.flush();
								
								command = "UPDATE_PLAYER_PROJECTILE\n";
								System.out.println("Sending: " + command);
								out.println(command);
								out.flush();
								
								command = "UPDATE_ENEMIES\n";
								System.out.println("Sending: " + command);
								out.println(command);
								out.flush();
//								
								command = "UPDATE_ENEMY_PROJECTILES\n";
								System.out.println("Sending: " + command);
								out.println(command);
								out.flush();

								command = "UPDATE_UFO\n";
								System.out.println("Sending: " + command);
								out.println(command);
								out.flush();
								
								command = "UPDATE_SCORE\n";
								System.out.println("Sending: " + command);
								out.println(command);
								out.flush();
								
								s2.close();	
								Thread.sleep(500);
							}
							catch(Exception e) {
								e.printStackTrace();
							}
						}	
					}
				}
			});
			t2.start( );
		}
		catch(Exception e2) {
			e2.printStackTrace();
		}

		//Action upon hitting close button:
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		
	} // End GameScreen constructor
	
//=====================================================================================================
//	PROGRAM MAIN
//=====================================================================================================
	
	public static void main(String args[]) throws IOException { 
		GameScreen1 myGameScreen = new GameScreen1();
		myGameScreen.setVisible(true); 
	}
	
//=====================================================================================================
//	GAMESCREEN FUNCTIONS
//=====================================================================================================

	public void stopGame() {
		// Stop the player & its projectile:
		player.stop(); // send stop command to server?
		playerProjectile.stop();
		
		// Hide player projectile:
		lbl_playerProjectile.setVisible(false);
		
		// Stop UFO spawning/movement:
		ufo.setStopThread(true);
		
		// Stop enemies & their projectiles, & hide enemy projectiles:
		for(int i = 0; i < GameProperties.ENEMY_ROWS; i++) {
			for(int j = 0; j < GameProperties.ENEMY_COLS; j++) {
				enemies[i][j].stop();
				enemies[i][j].setCanShoot(false);
				enemies[i][j].getEnemyProjectile().setStopProjectile(true);
				enemies[i][j].getEnemyProjectile().getLbl_prjct_enemy().setVisible(false);	
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
		stopGame();
		// Display "GAME OVER" msg:
		JOptionPane.showMessageDialog(null, "GAME OVER");
		String playerName = JOptionPane.showInputDialog("Please enter your name: ");
		while(playerName.trim().isEmpty()) {
			playerName = JOptionPane.showInputDialog("Please enter your name: ");
		}
		int playerScore = player.getPlayerScore();
		submitScore(playerName, playerScore);
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
		stopGame();
		// Display congratulatory msg:
		JOptionPane.showMessageDialog(null, "YOU STOPPED THE IVASION!");
		String playerName = JOptionPane.showInputDialog("Please enter your name: ");
		
		while(playerName.trim().isEmpty()) {
			playerName = JOptionPane.showInputDialog("Please enter your name: ");
		}
		
		int playerScore = awardLifeBonus(player.getPlayerScore(), player.getPlayerLives());
		submitScore(playerName, playerScore);
		System.exit(0);
	}
	
//=====================================================================================================
//	KEYBOARD CONTROLS
//=====================================================================================================
	
	@Override
	public void keyTyped(KeyEvent e) {
		//Key press & release treated as one
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//Key press only
		
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			if((player.getCanMove() == true) && (player.getX() - GameProperties.PLAYER_STEP) > 0) {
				
				try {
					// Set up a communication socket
					Socket s = new Socket("localhost", SERVER_PORT);
					
					// Initialize data stream to send data out
					OutputStream outstream = s.getOutputStream();
					out = new PrintWriter(outstream);
					
					// Command server to move player left
					command = "MOVE_PLAYER_LEFT\n";
					System.out.println("Sending: " + command);
					// "out" var defined in main --> out of bounds here!
						// FIX: defined staitc var "out" in class instead
					out.println(command);
					out.flush();
					s.close();	
				}
				catch(Exception e2) {
					e2.printStackTrace();
				}
			}	
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if((player.getCanMove() == true) && (player.getX() + player.getWidth() + GameProperties.PLAYER_STEP) < GameProperties.SCREEN_WIDTH) {
				
				try {
					// Set up a communication socket
					Socket s = new Socket("localhost", SERVER_PORT);
					
					// Initialize data stream to send data out
					OutputStream outstream = s.getOutputStream();
					out = new PrintWriter(outstream);
					
					// Command server to move player right
					command = "MOVE_PLAYER_RIGHT\n";
					System.out.println("Sending: " + command);
					out.println(command);
					out.flush();
					s.close();	
				}
				catch(Exception e3) {
					e3.printStackTrace();
				}
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_SPACE) {//Launch player projectile
				if((player.getCanMove()) && (!playerProjectile.getInMotion())) {
					playerProjectile.playSoundEffect(1);
					try {
						// Set up a communication socket
						Socket s = new Socket("localhost", SERVER_PORT);
						
						// Initialize data stream to send data out
						OutputStream outstream = s.getOutputStream();
						out = new PrintWriter(outstream);
						
						// Command server to launch player projectile
						command = "LAUNCH_PLAYER_PROJECTILE\n";
						System.out.println("Sending: " + command);
						out.println(command);
						out.flush();
						s.close();	
					}
					catch(Exception e4) {
						e4.printStackTrace();
					}
				}
		}	
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//Key release only	
	}	
	
} // End GameScreen1 Class
