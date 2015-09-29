/**
 * Created by Jalen on 9/9/2015.
 */
public class Constants {

  final int FLOOR = 0;
  final int WALL = 1;
  //NOTICE THIS IS 1 FOR NOW SO MAP LOADING DOESNT GET MESSED UP  WILL NEED TO HAVE MASK APPLIED LATER
  final int SCORCHED_WALL = 1;
  final int PILLAR = 2;
  final int EXIT = 3;
  final int SCORCHED_FLOOR = 5;
  final int BLACKNESS = 6;
  final int FIRETRAP = 7;

  static double PLAYER_DEFAULT_SPEED = 3.0;
  static double PLAYER_RUN_SPEED = 2.0;
  static double PLAYER_DEFAULT_STAMINA = 5.0;
  static double PLAYER_REGEN_STAMINA = .20;
  static double PLAYER_HEARING_DISTANCE = 10;
  static int PLAYER_SIGHT = 5;


  public void setPlayerSight(int newPlayerSight)
  {
    PLAYER_SIGHT = newPlayerSight;
  }
  public void setPlayerDefaultSpeed(double newPlayerSpeed)
  {
    PLAYER_DEFAULT_SPEED = newPlayerSpeed;
  }
  public void setPlayerRunSpeed(double newRunSpeed)
  {
    PLAYER_RUN_SPEED = newRunSpeed;
  }
  public void setPlayerHearing(double newPlayerHearing)
  {
    PLAYER_HEARING_DISTANCE = newPlayerHearing;
  }
  public void setPlayerStamina(double newDefaultStamina)
  {
    PLAYER_DEFAULT_STAMINA = newDefaultStamina;
  }
  public void setPlayerStaminaRegen(double newDefaultRegen)
  {
    PLAYER_REGEN_STAMINA = newDefaultRegen;
  }
  public void setZombieSpawnRate(double newZombieSpawnRate)
  {
    ZOMBIE_SPAWN_RATE = newZombieSpawnRate;
  }
  public void setZombieDefaultSpeed(double newDefaultSpeed)
  {
    ZOMBIE_DEFAULT_SPEED = newDefaultSpeed;
  }
  public void setZombieDecisionRate(int newDecisionRate)
  {
    ZOMBIE_DECISION_RATE = newDecisionRate;
  }
  public void setZombieSmell(int newZombieSmell)
  {
    ZOMBIE_SMELL = newZombieSmell;
  }
  public void setFiretrapSpawnRate(double newFiretrapSpawnRate)
  {
    FIRETRAP_SPAWN_RATE = newFiretrapSpawnRate;
  }

  public double getPLAYER_DEFAULT_SPEED()
  {
    return PLAYER_DEFAULT_SPEED;
  }
  final int PLAYER_SPRITE_HEIGHT = 60;
  final int PLAYER_SPRITE_WIDTH = 40;
  final int PLAYER_FEET_PIXEL_Y = 40;
  final int PLAYER_FEET_PIXEL_X = 3;

  final double PANVALUE_PADDING = .25;

  final int ANIMATION_DOWN_WALKING = 650;
  final int ANIMATION_TOP_WALKING = 525;
  final int ANIMATION_LEFT_WALKING = 587;
  final  int ANIMATION_RIGHT_WALKING = 715;
  final int ANIMATION_DEATH = 1294;
  final int ANIMATION_WIDTH = 33;
  final int ANIMATION_HEIGHT= 50;

  static double FIRETRAP_SPAWN_RATE = 0.010;
  final int FIRE_ANIMATION_HEIGHT = 60;
  final  int FIRE_ANIMATION_WIDTH = 40;

  static double PILLAR_SPAWN_RATE = 0.080;

  int SPRITE_SPREAD_DISTANCE = 64;

  static double MASTER_ZOMBIE_SPEED = 1.0;
  static double ZOMBIE_DEFAULT_SPEED = .50;
  static double ZOMBIE_SMELL = 7.0;
  static double ZOMBIE_SPAWN_RATE = .010;
  final double ZOMBIE_RANDOM_OR_LINE_RATE = .50;
  static int ZOMBIE_DECISION_RATE = 2000;

  int SIZE = 80;

  final int TOTAL_DIRECTIONS = 8;
  final int FOUR_CARDINAL_DIRECTIONS = 4;


  //used to find blocks around tile
  //Directions          NORTH, SOUTH, EAST, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
  //    DIRECTIONS   = {    0,     1,    2,    3,         4,         5,         6,         7};
  final int[] X_DIRECTIONS = {    0,     0,   -1,    1,        -1,         1,        -1,         1};
  final int[] Y_DIRECTIONS = {   -1,     1,    0,    0,        -1,        -1,         1,         1};

  final double SIN_OF_PI_4 = 0.70710678118;

  final int GUI_TIMER_SPEED = 33;


}
