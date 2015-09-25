import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jalen on 9/10/2015.
 * will hold the player/zombie objects/arrays, etc
 */
public class GameControl implements Constants
{

  static Player userPlayer;
 //static Zombie zombie1;
  ZombiePanel reference;

  ArrayList<Zombie> zombieList;
  private boolean movePlayerUp = false;
  private boolean movePlayerDown = false;
  private boolean movePlayerRight = false;
  private boolean movePlayerLeft = false;

  private Tile[][] mapCopy; //this will be used if the player dies to preserve the map

  Level level;


  Timer zombieReactionTimer = new Timer(ZOMBIE_DECISION_RATE, new ActionListener()
  {

    @Override
    public void actionPerformed(ActionEvent e)
    {


      //used for updating zombie direction every 2 sec
      for (Zombie zombie : zombieList)
      {
        if (zombie.isAlive())
        {
          zombie.updateDirection();
        }
      }
      //System.out.println("test counter "+testCount);
      //zombie1.updateDirection();
    }
  });

  Timer guiTimer = new Timer(GUI_TIMER_SPEED, new ActionListener()
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      if (userPlayer.canMove())
      {
        userPlayer.move(movePlayerUp, movePlayerDown, movePlayerRight, movePlayerLeft);
      }

      if (userPlayer.isRunning() && Level.map[userPlayer.getX()][userPlayer.getY()].getType() == FIRETRAP)
      {
        Level.map[userPlayer.getX()][userPlayer.getY()].explode();
        userPlayer.setAlive(false);
      }
      //if zombie hits player, reload map and players in same location
      //zombie1.move();

      for(Zombie zombie : zombieList)
      {

        if (zombie.isAlive() && zombie != null)
        {
          zombie.move();
          zombie.seeIfPlayerCanHear();
          Tile zombieLocation = Level.map[zombie.getX()][zombie.getY()];
          if (zombieLocation.getType() == FIRETRAP)
          {
            zombieLocation.explode();
          }

          //this has to be last so removing zombie is seemless?
          if (zombieLocation.isCombusting())
          {
            //todo be sure to save the original zombies probably in a seperate unused zombielist for level reloading on player death
            zombie.setAlive(false);
            zombie.zombieWalkSound.stop();
          }

          if(zombie.getX() == userPlayer.getX() && zombie.getY() == userPlayer.getY())
          {
            System.out.println("Player just died. Should be reloading");
            //set player death to true so we can reload the map
            reference.resetGame();
          }
        }
      }


      reference.repaint();
    }
  });

  public GameControl(ZombiePanel panel)
  {
    reference = panel;

    if(reference.gameState)
    {
      level = new Level(reference.getMapCopy());
      zombieList = reference.getZombieListCopy();

      level.setZombieList(zombieList);
      level.setStartX(reference.getStartX());
      level.setStartY(reference.getStartY());
      level.setExitX(reference.getExitX());
      level.setExitY(reference.getExitY());
      for(Zombie z: reference.getZombieListCopy())
      {
        z.setAlive(true);//make them visible on the map again
        z.setXPixel(z.getStartX()*SIZE);
        z.setYPixel(z.getStartY()*SIZE);
      }
      //TODO make sure the traps come back!
    }
    else
    {
      level = new Level();
    }

    mapCopy = level.getMapCopy();
    zombieList = level.getZombieList();
    reference.setMapCopy(level.getMapCopy());
    reference.setZombieListCopy(level.getZombieList());
    reference.setStartX(level.getStartRoomX());
    reference.setStartY(level.getStartRoomY());
    reference.setExitX(level.getExitRoomX());
    reference.setExitY(level.getStartExitY());

    userPlayer = new Player(level.getStartRoomX(), level.getStartRoomY());
    //zombie1 = new Zombie(9,9);
    //could possibly clone zombielist so level will always have the original info for reloading
    zombieReactionTimer.start();
    guiTimer.start();

  }

  public void setPlayerMoveUp(boolean b) { movePlayerUp = b; }
  public void setPlayerMoveDown(boolean b) { movePlayerDown = b; }
  public void setPlayerMoveRight(boolean b) { movePlayerRight = b; }
  public void setPlayerMoveLeft(boolean b) { movePlayerLeft = b; }

  public boolean checkIfPlayerMoving()
  {
    //if player is moving, play sound every sec
    //if stopped, then stop the timer
    if (movePlayerDown || movePlayerLeft || movePlayerRight || movePlayerUp)
    {
      return true;
    }
    return false;
  }
  public Player getUserPlayer()
  {
    return userPlayer;
  }


  //astar probably will go here.  Zombie arrays and players can be referenced from here

}
