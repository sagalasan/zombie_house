import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jalen on 9/10/2015.
 * will hold the player/zombie objects/arrays, etc
 */
public class GameControl extends Constants
{

  static Player userPlayer;
 //static Zombie zombie1;
  ZombiePanel reference;

  static ArrayList<Zombie> zombieList;
  Zombie masterZombie;
  String zombieStepsFileName = "sound_files/zombie_footsteps.wav";
  String zombieWallBump= "sound_files/wall_hit_zombie.wav";
  double walkTotalPanValue;
  double hitWallTotalPanValue;

  private Tile[][] mapCopy; //this will be used if the player dies to preserve the map

  Level level;

  Timer zombieWalkSound = new Timer(1000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      playSound(zombieStepsFileName, walkTotalPanValue);
    }
  });
  Timer zombieHitWallSound = new Timer(1000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      playSound(zombieWallBump, hitWallTotalPanValue);
    }
  });

  Timer zombieReactionTimer = new Timer(ZOMBIE_DECISION_RATE, new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent e)
    {
      masterZombie.setSmellPlayer(false);
      //used for updating zombie direction every 2 sec
      for (Zombie zombie : zombieList)
      {
        if (zombie.isAlive())
        {
          zombie.updateDirection();
          if (zombie.getSmellPlayer())
          {
            masterZombie.setSmellPlayer(true);
          }
        }
      }
      if (masterZombie.isAlive())
      {
        masterZombie.updateDirection();
      }
    }
  });

  Timer guiTimer = new Timer(GUI_TIMER_SPEED, new ActionListener()
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      if (userPlayer.canMove())
      {
        userPlayer.setAnimationDirectionBasedOnMovementBooleans();
        userPlayer.move();
      }
      //get feet of user player and test if feet are walking on the tile
      Tile playerTile = Level.map[userPlayer.getX()][userPlayer.getY()];
      //Tile abovePlayer = Level.map[userPlayer.getX()][userPlayer.getY()+1];
      if (userPlayer.isRunning() && userPlayer.getFeetBoundingRectangle().intersects(playerTile.getRectangleForFiretrap())
          && playerTile.getType() == FIRETRAP)
      {
        playerTile.explode();
      }
      if (playerTile.isCombusting() && userPlayer.getBoundingRectangleForFire().intersects(playerTile.getBoundingRectangle()))
      {

        reference.resetGame();
      }
      if (playerTile.getType() == EXIT)
      {
        //set the next level
        System.out.println("Found Exit!");
        reference.levelComplete();

      }

      boolean stepsZombieHeard = false;
      boolean hitWallZombieHeard = false;
      hitWallTotalPanValue = 0;
      walkTotalPanValue = 0;
      for(Zombie zombie : zombieList)
      {

        if (zombie.isAlive())
        {
          zombie.move();
          if (zombie.playerCanHearZombie())
          {
            if (zombie.hitwall())
            {
              hitWallZombieHeard = true;
              hitWallTotalPanValue += zombie.calculatePanValue();
            }
            else
            {
              stepsZombieHeard = true;
              walkTotalPanValue += zombie.calculatePanValue();
            }
          }
          Tile zombieLocation = Level.map[zombie.getX()][zombie.getY()];

          if (zombieLocation.getType() == FIRETRAP && zombie.getFeetBoundingRectangle().intersects(zombieLocation.getRectangleForFiretrap()))
          {
            zombieLocation.explode();
          }
          if (zombieLocation.isCombusting() && zombie.getBoundingRectangleForFire().intersects(zombieLocation.getBoundingRectangle()))
          {
            zombie.setAlive(false);
            zombie.startDeathAnimation();
          }


          if(zombie.getX() == userPlayer.getX() && zombie.getY() == userPlayer.getY())
          {
            //set player death to true so we can reload the map
            reference.resetGame();
          }
        }
      }
      walkTotalPanValue = adjustPanValue(walkTotalPanValue);
      hitWallTotalPanValue = adjustPanValue(hitWallTotalPanValue);
      //if there was a zombie that could be heard
      if (stepsZombieHeard)
      {
        if (!zombieWalkSound.isRunning())
        {
          zombieWalkSound.start();
        }
      }
      else
      {
        zombieWalkSound.stop();
      }
      if (hitWallZombieHeard)
      {
        if (!zombieHitWallSound.isRunning())
        {
          zombieHitWallSound.start();
        }
      }
      else
      {
        zombieHitWallSound.stop();
      }
      reference.repaint();
    }
  });

  public GameControl(ZombiePanel panel)
  {
    zombieReactionTimer.stop();
    guiTimer.stop();
    reference = panel;

    //System.out.println("level = " + reference.getLevelNumber());
    //System.out.println("gameState =  = " + reference.gameState);
    //The game restarts because youre dead
    if(reference.gameState)
    {
      level = new Level(reference.getLevelNumber(), reference.getMapCopy());
      zombieList = reference.getZombieListCopy();
      //masterZombie = reference.getMasterZombieCopy();

      masterZombie = zombieList.get(zombieList.size()-1);
      level.setMasterZombie(masterZombie);
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
      level = new Level(reference.getLevelNumber());

    }

    mapCopy = level.getMapCopy();
    zombieList = level.getZombieList();
    //masterZombie = level.getMasterZombie();
    masterZombie = zombieList.get(zombieList.size()-1);
    reference.setMapCopy(level.getMapCopy());
    reference.setZombieListCopy(level.getZombieList());

    reference.setMasterZombieCopy(masterZombie);
    reference.setStartX(level.getStartRoomX());
    reference.setStartY(level.getStartRoomY());
    reference.setExitX(level.getExitRoomX());
    reference.setExitY(level.getStartExitY());
    userPlayer = new Player(level.getStartRoomX(), level.getStartRoomY());
    //could possibly clone zombielist so level will always have the original info for reloading
    zombieReactionTimer.setInitialDelay(0);
    zombieReactionTimer.start();

    guiTimer.start();

  }

  public void playSound(String fileName, double panValue)
  {
    try
    {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(fileName).getAbsoluteFile());
      Clip clip = AudioSystem.getClip();
      clip.open(audioInputStream);
      FloatControl panControl = (FloatControl)clip.getControl(FloatControl.Type.PAN);
      panControl.setValue((float) panValue);
      clip.start();
    }
    catch(Exception ex)
    {
      System.out.println("Error with playing sound.");
      ex.printStackTrace();
    }
  }
  private double adjustPanValue(double panValue)
  {
    if (panValue > 1)
    {
      panValue = 1;
    }
    else if (panValue < -1)
    {
      panValue = -1;
    }
    return panValue;
  }

  /**
   *
   * @return returns true if any of the player booleans are true
   * returns false if the player booleans are all false.(the player is not moving)
   */
  public boolean checkIfPlayerMoving()
  {
    if (userPlayer.getMovePlayerDown() || userPlayer.getMovePlayerLeft() || userPlayer.getMovePlayerRight() || userPlayer.getMovePlayerUp())
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
