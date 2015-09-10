/**
 * Created by Jalen on 9/9/2015.
 */
public class Tile {
  //what the map uses as objects
  //will have a status
  int type;
  public Tile(int type){
    //could change this to for loop,
    //whatever is more readable
    //panel will read the number and draw block
    if (type == Constants.FLOOR)
    {
      this.type = Constants.FLOOR;
    }
    else if (type == Constants.WALL)
    {
      this.type = Constants.WALL;
    }
    else if (type == Constants.PILLAR)
    {
      this.type = Constants.PILLAR;
    }
    else if (type == Constants.EXIT)
    {
      this.type = Constants.EXIT;
    }
    else if (type == Constants.SCORCHED_FLOOR)
    {
      this.type = Constants.SCORCHED_FLOOR;
    }
    else
    {
      //black space
      this.type = Constants.BLACKNESS;
    }
  }
}
