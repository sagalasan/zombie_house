import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Jalen on 9/10/2015.
 * will hold the player/zombie objects/arrays, etc
 */
public class GameControl implements Constants
{

  static Player userPlayer;
  static Zombie zombie1;
  ZombiePanel reference;
  //adjust this to repaint faster

  ArrayList<Zombie> zombieList;
  private boolean movePlayerUp = false;
  private boolean movePlayerDown = false;
  private boolean movePlayerRight = false;
  private boolean movePlayerLeft = false;
  Level level = new Level();


  Timer zombieReactionTimer = new Timer(ZOMBIE_DECISION_RATE, new ActionListener()
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
    //  for (Zombie zombie : zombieList)
    //  {
    //    zombie.updateDirection();
     // }
     // zombie1.updateDirection();
     // System.out.println("zombie1 x and y " + zombie1.getX() + ", " + zombie1.getY());
    }
  });

  Timer guiTimer = new Timer(GUI_TIMER_SPEED, new ActionListener()
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      //System.out.println(userPlayer.getX());
      userPlayer.move(movePlayerUp, movePlayerDown, movePlayerRight, movePlayerLeft);
      //if zombie hits player, reload map and players in same location
    //  for (Zombie zombie : zombieList)
   //   {
        //System.out.println("zombie x " + zombie.getX());
    //    zombie.move();
    //  }
      reference.repaint();
    }
  });

  public GameControl(ZombiePanel panel)
  {
    reference = panel;
    userPlayer = new Player(5,5);
    //zombie1 = new Zombie(9,9);
    zombieList = level.zombieList;
    zombieReactionTimer.start();
    guiTimer.start();
  }

  public void setPlayerMoveUp(boolean b) { movePlayerUp = b; }
  public void setPlayerMoveDown(boolean b) { movePlayerDown = b; }
  public void setPlayerMoveRight(boolean b) { movePlayerRight = b; }
  public void setPlayerMoveLeft(boolean b) { movePlayerLeft = b; }

  public Player getUserPlayer()
  {
    return userPlayer;
  }


  //astar probably will go here.  Zombie arrays and players can be referenced from here

}
