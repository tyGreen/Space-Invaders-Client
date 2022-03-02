//===================================================================================================
// CLIENT-SIDE
//===================================================================================================


//Single file (class) containing several game properties:
public class GameProperties {
	
	// Screen dimensions:
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 800;
	
	// Scoreboard:
	public static final int SCORE_TXT_SIZE = 20;
	
	// Points System:
	public static final int PTS_PER_UFO = 150;
	public static final int PTS_PER_ENEMY1 = 30;
	public static final int PTS_PER_ENEMY2 = 20;
	public static final int PTS_PER_ENEMY3 = 10;
	public static final int NO_HIT_BONUS = 300;
	public static final int PTS_PER_BONUS_LIFE = 50;
	
	
	// # pixels sprites move upon trigger:
	public static final int PLAYER_WIDTH = 50;
	public static final int PLAYER_HEIGHT = 60;
	public static final int PLAYER_STEP = 40;
	
	public static final int PRJCT_PLAYER_STEP = 30;
	public static final int PRJCT_PLAYER_WIDTH = 25;
	public static final int PRJCT_PLAYER_HEIGHT = 30;
	
	public static final int ENEMY_WIDTH = 50;
	public static final int ENEMY_HEIGHT = 50;
	public static final int ENEMY_SPACING = 0;
	public static int ENEMY_STEP = 2;
	
	public static final int PRJCT_ENEMY_STEP = 20;
	public static final int PRJCT_ENEMY_WIDTH = 25;
	public static final int PRJCT_ENEMY_HEIGHT = 30;
	public static final int PRJCT_ENEMY_SHOT_FREQ = 25;
	
	public static final int UFO_WIDTH = 60;
	public static final int UFO_HEIGHT = 26;
	public static final int UFO_STEP = 20;
	public static final int UFO_SPAWN_FREQ = 75;
	public static final int UFO_SFX_DURATION = 50;

	// Player Life Icons:
	public static final int PLAYER_LIFE_ICON_WIDTH = 30;
	public static final int PLAYER_LIFE_ICON_HEIGHT = 30;

	// # rows & columns in ememy array:
	public static final int ENEMY_ROWS = 5;
	public static final int ENEMY_COLS = 11;
	public static int ENEMY_COUNT = (ENEMY_ROWS * ENEMY_COLS);
}
