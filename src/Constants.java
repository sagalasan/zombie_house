/**
 * Created by Jalen on 9/9/2015.
 */
public interface Constants {

  int FLOOR = 0;
  int WALL = 1;
  int PILLAR = 2;
  int EXIT = 3;
  int SCORCHED_FLOOR = 5;
  int BLACKNESS = 6;
  double PLAYER_DEFAULT_SPEED = 1.0;
  double PLAYER_RUN_SPEED = 2.0;
  double PLAYER_DEFAULT_STAMINA = 5.0;
  double PLAYER_REGEN_STAMINA = .20;
  double PLAYER_HEARING_DISTANCE = 10;
  double ZOMBIE_DEFAULT_SPEED = .50;
  double ZOMBIE_SMELL = 7.0;
  int SIZE = 80;
  int TOTAL_DIRECTIONS = 8;
  double ZOMBIE_SPAWN_RATE = .010;
  double ZOMBIE_RANDOM_OR_LINE_RATE = .50;
  int ZOMBIE_DECISION_RATE = 2000;
  //used to find blocks around tile
  //Directions          NORTH, SOUTH, EAST, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
  //    DIRECTIONS   = {    0,     1,    2,    3,         4,         5,         6,         7};
  int[] X_DIRECTIONS = {    0,     0,   -1,    1,        -1,         1,        -1,         1};
  int[] Y_DIRECTIONS = {   -1,     1,    0,    0,        -1,        -1,         1,         1};

  double SIN_OF_PI_4 = 0.70710678118;
  int GUI_TIMER_SPEED = 16;
}
