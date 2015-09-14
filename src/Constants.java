/**
 * Created by Jalen on 9/9/2015.
 */
public interface Constants {

  static final int FLOOR = 0;
  static final int WALL = 1;
  static final int PILLAR = 2;
  static final int EXIT = 3;
  static final int SCORCHED_FLOOR = 5;
  static final int BLACKNESS = 6;
  static final double PLAYER_DEFAULT_SPEED = 1;
  static final double ZOMBIE_DEFAULT_SPEED = .5;
  static final int SIZE = 20;
  static final int TOTAL_DIRECTIONS = 8;
  //used to find blocks around tile
  //Directions                       NORTH, SOUTH, EAST, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
  //                 DIRECTIONS   = {    0,     1,    2,    3,         4,         5,         6,         7};
  static final int[] X_DIRECTIONS = {    0,     0,   -1,    1,        -1,         1,        -1,         1};
  static final int[] Y_DIRECTIONS = {   -1,     1,    0,    0,        -1,        -1,         1,         1};

  static final double SIN_OF_PI_4 = 0.70710678118;
  static final int GUI_TIMER_SPEED = 16;
}
