//===================================================================================================
// CLIENT-SIDE
//===================================================================================================


import javax.swing.JLabel;

public class Enemy extends Sprite {
	
	private JLabel lbl_enemy;
	private JLabel[] lbl_playerLives;
	private Player myPlayer;
	private ProjectileEnemy enemyProjectile;
	private Boolean isRightBumper, isLeftBumper, isBottomBumper, hasFocus, gameOver, canShoot;
		// Flag to track if enemy's position within array is located on the perimeter 
		// (i.e. will it be one of the first enemies to reach a wall or the ground?)
	private Enemy[][] enemies;
	private int enemyID;
	
	public ProjectileEnemy getEnemyProjectile() {return enemyProjectile;}
	public void setEnemyProjectile(ProjectileEnemy enemyProjectile) {this.enemyProjectile = enemyProjectile;}
	
	public JLabel getLbl_enemy() {return lbl_enemy;}
	public void setLbl_enemy(JLabel lbl_enemy) {this.lbl_enemy = lbl_enemy;}
	
	public Boolean getGameOver() {return gameOver;}
	public void setGameOver(Boolean gameOver) {this.gameOver = gameOver;}
	
	public Enemy[][] getEnemies() {return enemies;}
	public void setEnemies(Enemy[][] enemies) {this.enemies = enemies;}
	
	public Boolean getHasFocus() {return hasFocus;}
	public void setHasFocus(Boolean hasFocus) {this.hasFocus = hasFocus;}
	
	public Boolean getIsRightBumper() {return isRightBumper;}
	public void setIsRightBumper(Boolean isRightBumper) {this.isRightBumper = isRightBumper;}
	
	public Boolean getIsLeftBumper() {return isLeftBumper;}
	public void setIsLeftBumper(Boolean isLeftBumper) {this.isLeftBumper = isLeftBumper;}
	
	public Boolean getIsBottomBumper() {return isBottomBumper;}
	public void setIsBottomBumper(Boolean isBottomBumper) {this.isBottomBumper = isBottomBumper;}
	
	public Boolean getCanShoot() {return canShoot;}
	public void setCanShoot(Boolean canShoot) {this.canShoot = canShoot;}
	
	public int getEnemyID() {return enemyID;}
	public void setEnemyID(int enemyID) {this.enemyID = enemyID;}
	
	//Constructors:
	public Enemy() {
		super(GameProperties.ENEMY_WIDTH, GameProperties.ENEMY_HEIGHT, "img_invader1.gif", true, true, false);
		this.enemyProjectile = new ProjectileEnemy(this, this.lbl_playerLives, this.myPlayer, this.enemies);
		this.isRightBumper = false;
		this.isLeftBumper = false;
		this.isBottomBumper = false;
		this.hasFocus = false;
		this.gameOver = false;
		this.canShoot = false;
		this.enemyID = 0;
		
	}
	
	public Enemy(int temp1, int temp2, JLabel temp3, Player temp4, Enemy[][] temp5, JLabel[] temp6) {
		super(GameProperties.ENEMY_WIDTH, GameProperties.ENEMY_HEIGHT, "img_invader1.gif", true, true, false);
		this.x = temp1;
		this.y = temp2;
		this.lbl_enemy = temp3;
		this.myPlayer = temp4;
		this.enemies = temp5;
		this.lbl_playerLives = temp6;
		this.enemyProjectile = new ProjectileEnemy(this, this.lbl_playerLives, this.myPlayer, this.enemies);
		this.isRightBumper = false;
		this.isLeftBumper = false;
		this.isBottomBumper = false;
		this.hasFocus = false;
		this.gameOver = false;
		this.canShoot = false;
		this.enemyID = 0;
	}
	
	//Other methods:
	@Override
	public void hide() {this.lbl_enemy.setVisible(false);}
	
	@Override
	public void show() {this.lbl_enemy.setVisible(true);}
	
	public void display() {System.out.println("x,y / visible?: " + this.x + "," + this.y + " / " + this.visible);}
	
} // End Class