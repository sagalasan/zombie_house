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
public class GameControl implements Constants
{

  static Player userPlayer;
 //static Zombie zombie1;
  ZombiePanel reference;

  ArrayList<Zombie> zombieList;

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
    public void actionPerformed(ActionEvent e) {


      //used for updating zombie direction every 2 sec
      for (Zombie zombie : zombieList) {
        if (zombie.isAlive()) {
          zombie.updateDirection();
        }
      }
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
        userPlayer.setAnimationDirectionBasedOnMovementBooleans();
        userPlayer.move();
      }

      if (userPlayer.isRunning() && Level.map[userPlayer.getX()][userPlayer.getY()].getType() == FIRETRAP)
      {
        Level.map[userPlayer.getX()][userPlayer.getY()].explode();
        userPlayer.setAlive(false);
      }
      //if zombie hits player, reload map and players in same location
      //zombie1.move();
      boolean stepsZombieHeard = false;
      boolean hitWallZombieHeard = false;
      hitWallTotalPanValue = 0;
      walkTotalPanValue = 0;
      for(Zombie zombie : zombieList)
      {
        if (zombie.isAlive())// && zombie != null)
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
          if (zombieLocation.getType() == FIRETRAP)
          {
            zombieLocation.explode();
          }
          //this has to be last so removing zombie is seemless?
          if (zombieLocation.isCombusting())
          {
            //todo be sure to save the original zombies probably in a seperate unused zombielist for level reloading on player death
            zombie.setAlive(false);
            //zombie.zombieWalkSound.stop();
          }
          if(zombie.getX() == userPlayer.getX() && zombie.getY() == userPlayer.getY())
          {
            System.out.println("Player just died. Should be reloading");
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

  public void playSound(String fileName, double panValue)
  {
    try {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(fileName).getAbsoluteFile());
      Clip clip = AudioSystem.getClip();
      clip.open(audioInputStream);
      FloatControl panControl = (FloatControl)clip.getControl(FloatControl.Type.PAN);
      panControl.setValue((float) panValue);
      //FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
      //gainControl.setValue(-1 * gainValue);
      //System.out.println("gainvalue "+gainValue);
      clip.start();
    } catch(Exception ex) {
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
  public boolean checkIfPlayerMoving()
  {
    //if player is moving, play sound every sec
    //if stopped, then stop the timer
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
