/**
 * Created by Jalen on 9/9/2015.
 * @Level
 * prcedurally generate map
 * 2d array of tile objects
 * floor, pillar, exit, walls, fire
 *
 */

public class Level {
  static Tile[][] map;
  static int height = 30, width = 40;

  public Level(){
    //creates map, use helper methods later
    map = new Tile[width][height];
    for (int i = 0; i < width; i++)
    {
      for (int j = 0; j < height; j++)
      {
        if (i == 0 || j == 0 || i == width - 1 || j == height - 1)
        {
          map[i][j] = new Tile(Constants.WALL, i, j);
        }
        else
        {
          map[i][j] = new Tile(Constants.FLOOR, i, j);
        }
      }
    }
  }

}
